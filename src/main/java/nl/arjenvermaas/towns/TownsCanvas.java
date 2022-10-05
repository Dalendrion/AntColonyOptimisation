package nl.arjenvermaas.towns;

import nl.arjenvermaas.animation.CanvasPanel;
import nl.arjenvermaas.math.MyMath;
import nl.arjenvermaas.matrix.FloatMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class TownsCanvas extends CanvasPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(TownsCanvas.class);

    private enum State {
        INIT_PATHS,
        FIND_ROAD,
        ANIMATE_ANTS,
        ANIMATE_ANTS_RETURN_HOME,
        DECIDE_PATH,
        RESET_PATHS,
        DONE
    }

    private static final State initialState = State.INIT_PATHS;
    private static final State initialSubState = null;

    private static final float animationSpeed = 5f;
    private static final float THRESHOLD = 0.0001f;

    private final AcoConfig config;

    private State state = initialState;
    private State subState = initialSubState;
    private float animationProgress = 1;
    private List<Town> towns;
    private FloatMatrix distances;
    private FloatMatrix pheromones;
    private List<List<Integer>> paths;
    private List<Integer> shortestPath;

    public TownsCanvas(int width, int height, AcoConfig config) {
        this(width, height, config, 60);
    }

    public TownsCanvas(int width, int height, AcoConfig config, int frameRate) {
        super(width, height, frameRate);
        this.config = config;
    }

    @Override
    public void reset() {
        towns = createTowns(config.numTowns());
        distances = createDistances(towns);
        resetPaths();
    }

    public void resetPaths() {
        pheromones = createPheromones(towns, config.initialPheromone());
        paths = null;
        shortestPath = null;
        state = initialState;
        subState = initialSubState;
        animationProgress = 0;
    }

    public void resetPathsWhenReady() {
        subState = State.RESET_PATHS;
    }

    public void decidePath() {
        subState = State.DECIDE_PATH;
    }

    @Override
    protected void update(long delta_time) {
        switch (state) {
            case DONE:
                break;
            case INIT_PATHS:
                paths = createInitialPaths(config.numAnts(), config.numTowns());
                state = State.FIND_ROAD;
                break;
            case FIND_ROAD:
                List<List<Integer>> remainingTownsPerPath = getRemainingTownsPerPath(paths, config.numTowns());
                if (remainingTownsPerPath.get(0).isEmpty()) {
                    animationProgress = 0;
                    changeState(State.ANIMATE_ANTS_RETURN_HOME);
                    return;
                }
                List<List<Float>> desirabilities = getDesirabilities(remainingTownsPerPath, paths, distances, pheromones, config.distStrength(), config.distPower(), config.pheromonePower());
                List<Integer> chosenTowns = chooseNewTowns(remainingTownsPerPath, desirabilities);
                moveAnts(chosenTowns, paths);
                animationProgress = 0;
                changeState(State.ANIMATE_ANTS);
                break;
            case ANIMATE_ANTS:
                animationProgress = MyMath.constrain(animationProgress + animationSpeed / 1000 * delta_time, 0, 1);
                if (animationProgress == 1) {
                    changeState(State.FIND_ROAD);
                }
                break;
            case ANIMATE_ANTS_RETURN_HOME:
                animationProgress = MyMath.constrain(animationProgress + animationSpeed / 1000 * delta_time, 0, 1);
                if (animationProgress == 1) {
                    depositPheromone(paths, distances, pheromones, config.pheromoneIntensity());
                    evaporatePheromone(pheromones, config.pheromoneEvaporationRate());
                    shortestPath = findShortestPath(paths, distances, shortestPath);
                    if (checkPathsDone(pheromones)) {
                        changeState(State.DONE);
                        return;
                    }
                    LOGGER.info(pheromones.toString());
                    changeState(State.INIT_PATHS);
                }
                break;
            case DECIDE_PATH:
                pheromones.apply(f -> 0f);
                for (int i = 0; i < shortestPath.size(); i++) {
                    pheromones.set(shortestPath.get(i), shortestPath.get((i + 1) % shortestPath.size()), 1f);
                }
                changeState(State.DONE);
                break;
            case RESET_PATHS:
                resetPaths();
                changeState(State.INIT_PATHS);
                break;
        }
    }

    private List<Integer> findShortestPath(List<List<Integer>> paths, FloatMatrix distances, List<Integer> shortestPath) {
        float minDistance = getPathLength(shortestPath, distances);
        for (List<Integer> path : paths) {
            float dist = getPathLength(path, distances);
            if (dist < minDistance) {
                minDistance = dist;
                shortestPath = path;
            }
        }
        return shortestPath;
    }

    private void changeState(State newState) {
        if (subState != null && newState == State.INIT_PATHS) {
            state = subState;
            subState = null;
            return;
        }
        if (state == newState) {
            return;
        }
        state = newState;
    }

    @Override
    protected void draw(Graphics g) {
        background(g, Color.BLACK);
        g.fillRect(0, 0, getCanvasWidth(), getCanvasHeight());

        drawRoads(g);
        drawTowns(g);
        drawAnts(g);
    }

    private void background(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, getCanvasWidth(), getCanvasHeight());
    }
    
    private void drawRoads(Graphics g) {
        float maxPheromone = pheromones.getData().stream().max(Float::compareTo).orElse(0F);
        float minPheromone = pheromones.getData().stream().min(Float::compareTo).orElse(0F);

        List<float[]> map = new ArrayList<>();

        for (int row = 0; row < pheromones.getNumRows(); row++) {
            for (int col = 0; col < pheromones.getNumCols(); col++) {
                if (row == col) {
                    continue;
                }
                float value;
                try {
                    Float pheromone = pheromones.get(row, col);
                    value = MyMath.map(pheromone, minPheromone, maxPheromone, 0.2f, 1);
                    if (pheromone < THRESHOLD) {
                        value = 0;
                    }
                } catch (IllegalArgumentException e) {
                    value = 0.5f;
                }
                Town from = towns.get(row);
                Town to = towns.get(col);
                map.add(new float[] {value, from.x(), from.y(), to.x(), to.y()});
            }
        }
        map.stream()
                .sorted(Comparator.comparing(array -> array[0]))
                .forEach(array -> {
                    float value = array[0];
                    float x1 = array[1];
                    float y1 = array[2];
                    float x2 = array[3];
                    float y2 = array[4];
                    Color lineColor = MyMath.lerp(Color.BLACK, Color.WHITE, value);
                    g.setColor(lineColor);
                    g.drawLine(java.lang.Math.round(x1), java.lang.Math.round(y1), java.lang.Math.round(x2), java.lang.Math.round(y2));
                });
    }

    private void drawTowns(Graphics g) {
        Font font = g.getFont();
        Font font1 = font.deriveFont(Font.BOLD, 16f);
        g.setFont(font1);
        int index = 0;
        for (Town town : towns) {
            g.setColor(Color.WHITE);
            float d = town.d();
            float x = town.x();
            float y = town.y();
            g.fillOval(java.lang.Math.round(x - d / 2), java.lang.Math.round(y - d / 2), java.lang.Math.round(d), java.lang.Math.round(d));
            g.setColor(Color.YELLOW);
            g.drawString(String.valueOf(index), Math.round(x + d / 2 + 1), Math.round(y));
            index++;
        }
    }

    private void drawAnts(Graphics g) {
        if (paths == null) {
            return;
        }
        for (List<Integer> path : paths) {
            int currentIndex;
            int previousIndex;
            if (state == State.ANIMATE_ANTS_RETURN_HOME) {
                previousIndex = path.get(java.lang.Math.max(0, path.size() - 1));
                currentIndex = path.get(0);
            } else {
                currentIndex = path.get(java.lang.Math.max(0, path.size() - 1));
                previousIndex = path.get(java.lang.Math.max(0, path.size() - 2));
            }
            Town currentTown = towns.get(currentIndex);
            Town previousTown = towns.get(previousIndex);
            g.setColor(Color.RED);
            float d = 8;
            float x = MyMath.lerp(previousTown.x(), currentTown.x(), animationProgress);
            float y = MyMath.lerp(previousTown.y(), currentTown.y(), animationProgress);
            g.fillOval(java.lang.Math.round(x - d / 2), java.lang.Math.round(y - d / 2), java.lang.Math.round(d), java.lang.Math.round(d));
        }
    }

    private List<Town> createTowns(int amount) {
        List<Town> townList = new ArrayList<>();
        float radius = 10;
        int margin = 50;

        for (int i = 0; i < amount; i++) {
            Town newTown;
            boolean found;
            do {
                float x = margin + (float) java.lang.Math.random() * (getCanvasWidth() - margin * 2);
                float y = margin + (float) java.lang.Math.random() * (getCanvasHeight() - margin * 2);
                found = townList.stream().noneMatch(town -> MyMath.dist(town.x(), town.y(), x, y) < 3 * radius);
                newTown = new Town(i, x, y, radius);
            } while (!found);
            townList.add(newTown);
        }
        return townList;
    }

    private FloatMatrix createDistances(List<Town> towns) {
        FloatMatrix distances = new FloatMatrix(towns.size(), towns.size());
        for (int row = 0; row < towns.size(); row++) {
            for (int col = 0; col < towns.size(); col++) {
                Town from = towns.get(row);
                Town to = towns.get(col);
                float dist = MyMath.dist(from.x(), from.y(), to.x(), to.y());
                distances.set(row, col, dist);
            }
        }
        return distances;
    }

    private FloatMatrix createPheromones(List<Town> towns, float initialPheromone) {
        FloatMatrix distances = new FloatMatrix(towns.size(), towns.size());
        for (int row = 0; row < towns.size(); row++) {
            for (int col = 0; col < towns.size(); col++) {
                distances.set(row, col, initialPheromone);
            }
        }
        return distances;
    }

    private List<List<Integer>> createInitialPaths(int numAnts, int numTowns) {
        List<List<Integer>> paths = new ArrayList<>();
        for (int i = 0; i < numAnts; i++) {
            int townIndex = (int) (java.lang.Math.random() * numTowns);
            ArrayList<Integer> path = new ArrayList<>();
            path.add(townIndex);
            paths.add(path);
        }
        return paths;
    }

    private List<List<Integer>> getRemainingTownsPerPath(List<List<Integer>> paths, int numTowns) {
        List<List<Integer>> remainingTowns = new ArrayList<>();
        for (List<Integer> path : paths) {
            List<Integer> remainingTownsForAnt = IntStream.range(0, numTowns)
                    .filter(town -> !path.contains(town))
                    .boxed()
                    .toList();
            remainingTowns.add(remainingTownsForAnt);
        }
        return remainingTowns;
    }

    private List<List<Float>> getDesirabilities(List<List<Integer>> paths, List<List<Integer>> remainingTownsPerPath, FloatMatrix distances, FloatMatrix pheromones, float distStrength, float distPower, float pheromonePower) {
        List<List<Float>> desirabilities = new ArrayList<>();
        for (int ant = 0; ant < paths.size(); ant++) {
            List<Float> desirabilitiesRow = new ArrayList<>();
            List<Integer> path = paths.get(ant);
            List<Integer> remainingTownsList = remainingTownsPerPath.get(ant);
            int currentTownIndex = path.get(path.size() - 1);
            for (int townIndex : remainingTownsList) {
                float distance = distances.get(currentTownIndex, townIndex);
                float pheromone = pheromones.get(currentTownIndex, townIndex);
                float desirability = (float) (java.lang.Math.pow(distStrength / distance, distPower) * java.lang.Math.pow(pheromone, pheromonePower));
                desirabilitiesRow.add(desirability);
            }
            desirabilities.add(desirabilitiesRow);
        }
        return desirabilities;
    }

    private List<Integer> chooseNewTowns(List<List<Integer>> remainingTowns, List<List<Float>> desirabilities) {
        List<Integer> choices = new ArrayList<>();
        for (int ant = 0; ant < remainingTowns.size(); ant++) {
            List<Integer> remainingTownsForAnt = remainingTowns.get(ant);
            List<Float> desirabilitiesForAnt = desirabilities.get(ant);
            choices.add(choose(remainingTownsForAnt, desirabilitiesForAnt));
        }
        return choices;
    }


    private void moveAnts(List<Integer> newTowns, List<List<Integer>> paths) { // impure
        for (int ant = 0; ant < paths.size(); ant++) {
            List<Integer> pathForAnt = paths.get(ant);
            Integer newTownForAnt = newTowns.get(ant);
            pathForAnt.add(newTownForAnt);
        }
    }

    private void depositPheromone(List<List<Integer>> paths, FloatMatrix distances, FloatMatrix pheromones, float pheromoneIntensity) { // impure
        for (List<Integer> pathForAnt : paths) {
            float distance = getPathLength(pathForAnt, distances);
            for (int i = 0; i < pathForAnt.size(); i++) {
                int currentTownIndex = pathForAnt.get(i);
                int nextTownIndex = pathForAnt.get((i + 1) % pathForAnt.size());
                Float pheromone = pheromones.get(currentTownIndex, nextTownIndex);
                pheromone += pheromoneIntensity / distance;
                pheromones.set(currentTownIndex, nextTownIndex, pheromone);
                pheromones.set(nextTownIndex, currentTownIndex, pheromone);
            }
        }
    }

    private float getPathLength(List<Integer> path, FloatMatrix distances) {
        float distance = 0;
        for (int i = 0; i < path.size(); i++) {
            int currentTownIndex = path.get(i);
            int nextTownIndex = path.get((i + 1) % path.size());
            distance += distances.get(currentTownIndex, nextTownIndex);
        }
        return distance;
    }

    private void evaporatePheromone(FloatMatrix pheromones, float pheromoneEvaporationRate) { // impure
        pheromones.apply(f -> Math.max(0, (1 - pheromoneEvaporationRate) * f));
    }

    private boolean checkPathsDone(FloatMatrix pheromones) {
        for (int row = 0; row < pheromones.getNumRows(); row++) {
            List<Float> pheromonesRow = pheromones.getRow(row);
            long count = pheromonesRow.stream().filter(f -> f > THRESHOLD).count();
            if (count > 2) {
                return false;
            }
        }
        return true;
    }

    public <T> T choose(List<T> options, List<Float> weights) {
        int randomIndex = MyMath.weightedRandomIndex(weights.stream().mapToDouble(x -> x).toArray());
        return options.get(randomIndex);
    }
}

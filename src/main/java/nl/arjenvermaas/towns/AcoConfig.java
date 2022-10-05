package nl.arjenvermaas.towns;

public record AcoConfig(int numTowns,
                        int numAnts,
                        float distPower,
                        float distStrength,
                        float initialPheromone,
                        float pheromonePower,
                        float pheromoneIntensity,
                        float pheromoneEvaporationRate) {

    public static class AcoConfigBuilder {
        private int numTowns = 10;
        private int numAnts = 20;
        private float distPower = 10;
        private float distStrength = 1;
        private float initialPheromone = 1.0f;
        private float pheromonePower = 4;
        private float pheromoneIntensity = 10;
        private float pheromoneEvaporationRate = 0.2f; // From 0 to 1

        public AcoConfigBuilder withNumTowns(int numTowns) {
            this.numTowns = numTowns;
            return this;
        }

        public AcoConfigBuilder withNumAnts(int numAnts) {
            this.numAnts = numAnts;
            return this;
        }

        public AcoConfigBuilder withDistPower(float distPower) {
            this.distPower = distPower;
            return this;
        }

        public AcoConfigBuilder withDistStrength(float distStrength) {
            this.distStrength = distStrength;
            return this;
        }

        public AcoConfigBuilder withInitialPheromone(float initialPheromone) {
            this.initialPheromone = initialPheromone;
            return this;
        }

        public AcoConfigBuilder withPheromonePower(float pheromonePower) {
            this.pheromonePower = pheromonePower;
            return this;
        }

        public AcoConfigBuilder withPheromoneIntensity(float pheromoneIntensity) {
            this.pheromoneIntensity = pheromoneIntensity;
            return this;
        }

        public AcoConfigBuilder withPheromoneEvaporationRate(float pheromoneEvaporationRate) {
            if (pheromoneEvaporationRate < 0 || pheromoneEvaporationRate > 1) {
                throw new IllegalArgumentException("Evaporation rate is a ratio from 0 to 1, both inclusive.");
            }
            this.pheromoneEvaporationRate = pheromoneEvaporationRate;
            return this;
        }

        public AcoConfig build() {
            return new AcoConfig(
                    numTowns,
                    numAnts,
                    distPower,
                    distStrength,
                    initialPheromone,
                    pheromonePower,
                    pheromoneIntensity,
                    pheromoneEvaporationRate
            );
        }
    }
}

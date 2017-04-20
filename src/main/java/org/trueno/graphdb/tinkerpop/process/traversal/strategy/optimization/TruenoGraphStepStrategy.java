package org.trueno.graphdb.tinkerpop.process.traversal.strategy.optimization;

import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.NoOpBarrierStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.AbstractTraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;

import org.trueno.graphdb.tinkerpop.process.traversal.step.sideEffect.TruenoGraphStep;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 *
 * Provider Optimization strategy implemented as explained on TinkerPop documentation.
 *
 * @see {<a href="http://tinkerpop.apache.org/docs/current/reference/#traversalstrategy">TraversalStrategy</a>}
 */
public final class TruenoGraphStepStrategy extends
        AbstractTraversalStrategy<TraversalStrategy.ProviderOptimizationStrategy>
        implements TraversalStrategy.ProviderOptimizationStrategy {

    private static final TruenoGraphStepStrategy INSTANCE = new TruenoGraphStepStrategy();

    private TruenoGraphStepStrategy() {
    }

    @Override
    public void apply(Traversal.Admin<?, ?> traversal) {
        if (TraversalHelper.onGraphComputer(traversal))
            return;

        for (final GraphStep originalGraphStep : TraversalHelper.getStepsOfClass(GraphStep.class, traversal)) {
            final TruenoGraphStep<?, ?> truenoGraphStep = new TruenoGraphStep<>(originalGraphStep);
            TraversalHelper.replaceStep(originalGraphStep, truenoGraphStep, traversal);
            Step<?, ?> currentStep = truenoGraphStep.getNextStep();
            while (currentStep instanceof HasStep || currentStep instanceof NoOpBarrierStep) {
                if (currentStep instanceof HasStep) {
                    for (final HasContainer hasContainer : ((HasContainerHolder) currentStep).getHasContainers()) {
                        if (!GraphStep.processHasContainerIds(truenoGraphStep, hasContainer))
                            truenoGraphStep.addHasContainer(hasContainer);
                    }
                    TraversalHelper.copyLabels(currentStep, currentStep.getPreviousStep(), false);
                    traversal.removeStep(currentStep);
                }
                currentStep = currentStep.getNextStep();
            }
        }
    }

    public static TruenoGraphStepStrategy instance() {
        return INSTANCE;
    }
}

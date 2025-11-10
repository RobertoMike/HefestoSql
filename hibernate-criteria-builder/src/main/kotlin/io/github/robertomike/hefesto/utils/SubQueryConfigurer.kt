package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.builders.BaseBuilder
import java.util.function.Consumer

/**
 * Helper class to reduce duplication in subquery configuration logic.
 * Provides a centralized way to create and configure subqueries with lambda blocks.
 */
object SubQueryConfigurer {
    
    /**
     * Creates and configures a subquery using a lambda block.
     * This eliminates the repetitive pattern of:
     * 1. Create subquery builder
     * 2. Create context
     * 3. Accept consumer block
     *
     * @param subQueryModel The entity class for the subquery
     * @param builderFactory Function that creates a builder instance from a model class
     * @param block Lambda to configure the subquery
     * @return The configured subquery builder
     */
    fun <S, BUILDER> configureSubQuery(
        subQueryModel: Class<S>,
        builderFactory: (Class<S>) -> BUILDER,
        block: Consumer<SubQueryContext<BUILDER>>
    ): BUILDER where BUILDER : BaseBuilder<*, *, *, *, *, *, *, *>, BUILDER : SharedMethods<BUILDER> {
        val subQuery = builderFactory(subQueryModel)
        val context = SubQueryContext(subQuery)
        block.accept(context)
        return subQuery
    }
}
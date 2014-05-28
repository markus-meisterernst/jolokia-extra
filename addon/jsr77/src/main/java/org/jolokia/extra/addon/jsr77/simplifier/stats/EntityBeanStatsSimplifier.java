/*
 *  Copyright 2012 Marcin Plonka
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


/**
 * @author mplonka
 */
package org.jolokia.extra.addon.jsr77.simplifier.stats;

import javax.management.j2ee.statistics.EntityBeanStats;
import javax.management.j2ee.statistics.RangeStatistic;

public class EntityBeanStatsSimplifier extends EJBStatsSimplifier<EntityBeanStats> {

    @SuppressWarnings("unchecked")
    public EntityBeanStatsSimplifier() {
        super(EntityBeanStats.class);

    	addExtractor("pooledCount",
                     new PooledCountExtractor());
		addExtractor("readyCount",
                     new ReadyCountExtractor());
	}

    private static class PooledCountExtractor implements AttributeExtractor<EntityBeanStats> {
        public RangeStatistic extract(EntityBeanStats o) {
            return o.getPooledCount();
        }
    }

    private static class ReadyCountExtractor implements AttributeExtractor<EntityBeanStats> {
        public RangeStatistic extract(EntityBeanStats o) {
            return o.getReadyCount();
        }
    }
}
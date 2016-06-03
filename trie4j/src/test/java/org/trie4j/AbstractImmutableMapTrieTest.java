/*
 * Copyright 2012 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trie4j;

import org.trie4j.patricia.MapTailPatriciaTrie;

public abstract class AbstractImmutableMapTrieTest<T extends MapTrie<Integer>>
extends AbstractMapTrieTest<MapTailPatriciaTrie<Integer>, T> {
	protected abstract T buildSecond(MapTrie<Integer> firstTrie);

	@Override
	protected MapTailPatriciaTrie<Integer> createFirstTrie() {
		return new MapTailPatriciaTrie<>();
	}
	
	@Override
	protected T buildSecondTrie(MapTailPatriciaTrie<Integer> firstTrie) {
		return buildSecond(firstTrie);
	}
}

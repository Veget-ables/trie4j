/*
 * Copyright 2014 Takao Nakaguchi
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
package org.trie4j.tail;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.trie4j.tail.index.TailIndex;

public class ImmutableTailArray
implements Externalizable, TailArray{
	public ImmutableTailArray() {
	}

	public ImmutableTailArray(CharSequence tail, TailIndex tailIndex) {
		this.tail = tail;
		this.tailIndex = tailIndex;
	}

	public TailIndex getTailIndex(){
		return tailIndex;
	}

	@Override
	public TailCharIterator newIterator(int offset) {
		return new TailCharIterator(tail, offset);
	}

	@Override
	public TailCharIterator newIterator() {
		return new TailCharIterator(tail, -1);
	}

	@Override
	public int getIteratorOffset(int nodeId) {
		return tailIndex.get(nodeId);
	}

	@Override
	public void getChars(StringBuilder builder, int nodeId) {
		int offset = tailIndex.get(nodeId);
		if(offset == -1) return;
		TailUtil.appendChars(tail, offset, builder);
	}

	@Override
	public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
		tail = (CharSequence)in.readObject();
		tailIndex = (TailIndex)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(tail);
		out.writeObject(tailIndex);
	}

	private CharSequence tail;
	private TailIndex tailIndex;
}
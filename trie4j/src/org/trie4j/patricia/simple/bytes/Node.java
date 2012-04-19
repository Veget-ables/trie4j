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
package org.trie4j.patricia.simple.bytes;

import java.util.Arrays;

public class Node{
	public Node() {
	}
	public Node(byte[] letters, boolean terminated) {
		this.letters = letters;
		this.terminated = terminated;
	}
	public Node(byte[] letters, Node[] children, boolean terminated) {
		this.children = children;
		this.letters = letters;
		this.terminated = terminated;
	}
	public Node[] getChildren() {
		return children;
	}
	public void setChildren(Node[] children) {
		this.children = children;
	}
	public byte[] getLetters() {
		return letters;
	}
	public void setLetters(byte[] letters) {
		this.letters = letters;
	}
	public boolean isTerminated() {
		return terminated;
	}
	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}
	public Node getChild(byte c){
		if(children != null){
			int end = children.length;
			if(end > 16){
				int start = 0;
				while(start < end){
					int i = (start + end) / 2;
					Node n = children[i];
					int d = c - n.letters[0];
					if(d == 0) return n;
					if(d < 0){
						end = i;
					} else if(start == i){
						break;
					} else{
						start = i;
					}
				}
			} else{
				for(int i = 0; i < end; i++){
					Node n = children[i];
					if(n.letters != null && n.letters.length > 0 && n.letters[0] == c){
						return n;
					}
				}
			}
		}
		return null;
	}
	public void insertChild(byte[] letters, int offset){
		if(this.letters == null){
			this.letters = Arrays.copyOfRange(letters, offset, letters.length);
			this.terminated = true;
			return;
		}
//hello$
//h -> insert to children (a)
//h$ -> insert to children (b)
//hat -> h, at, ello$ (c)
//hat$ -> h, at$, ello$ (d)
//hello -> hello$ (e)
//hello$ -> hello$ (f)
//helloworld$ -> hello$, helloworld$ (g)
//helloworld (h)
		int i = 0;
		int lettersRest = letters.length - offset;
		int thisLettersLength = this.letters.length;
		int n = Math.min(lettersRest, thisLettersLength);
		int c = 0;
		while(i < n && (c = letters[i + offset] - this.letters[i]) == 0) i++;
		if(i == n){
			if(lettersRest == thisLettersLength){
				this.terminated = true;
				return;
			}
			if(lettersRest < thisLettersLength){
				Node child = new Node(
						Arrays.copyOfRange(this.letters, lettersRest, this.letters.length)
						, this.children, this.terminated);
				this.letters = Arrays.copyOfRange(this.letters, 0, i);
				this.children = new Node[]{child};
				this.terminated = true;
				return;
			}
			if(children != null){
				int index = 0;
				int end = children.length;
				if(end > 16){
					int start = 0;
					while(start < end){
						index = (start + end) / 2;
						Node child = children[index];
						c = letters[i + offset] - child.letters[0];
						if(c == 0){
							child.insertChild(letters, i + offset);
							return;
						}
						if(c < 0){
							end = index;
						} else if(start == index){
							index = end;
							break;
						} else{
							start = index;
						}
					}
				} else{
					for(; index < end; index++){
						Node child = children[index];
						c = letters[i + offset] - child.letters[0];
						if(c < 0) break;
						if(c == 0){
							child.insertChild(letters, i + offset);
							return;
						}
					}
				}
				addChild(index, new Node(Arrays.copyOfRange(letters, i + offset, letters.length), true));
			} else{
				this.children = new Node[]{
						new Node(Arrays.copyOfRange(letters, i + offset, letters.length), true)
						};
				this.terminated = true;
			}
			return;
		}
		byte[] newLetter1 = Arrays.copyOfRange(this.letters, 0, i);
		byte[] newLetter2 = Arrays.copyOfRange(this.letters, i, this.letters.length);
		byte[] newLetter3 = Arrays.copyOfRange(letters, i + offset, letters.length);
		Node[] newChildren = new Node[2];
		if(newLetter2[0] < newLetter3[0]){
			newChildren[0] = new Node(newLetter2, this.children, true);
			newChildren[1] = new Node(newLetter3, true);
		} else{
			newChildren[0] = new Node(newLetter3, true);
			newChildren[1] = new Node(newLetter2, this.children, true);
		}
		this.letters = newLetter1;
		this.children = newChildren;
		this.terminated = false;
	}
	public boolean contains(byte[] letters, int offset){
		int rest = letters.length - offset;
		int tll = this.letters.length;
		if(tll > rest) return false;
		for(int i = 0; i < tll; i++){
			if(this.letters[i] != letters[i + offset]) return false;
		}
		if(tll == rest){
			return terminated;
		}
		offset += tll;
		byte c = letters[offset];
		Node n = getChild(c);
		if(n != null){
			return n.contains(letters, offset);
		}
		return false;
	}
	public void visit(TrieVisitor visitor, int nest){
		visitor.accept(this, nest);
		nest++;
		if(children != null){
			for(Node n : children){
				n.visit(visitor, nest);
			}
		}
	}
	private void addChild(int index, Node n){
		Node[] newc = new Node[children.length + 1];
		System.arraycopy(children,  0, newc, 0, index);
		newc[index] = n;
		System.arraycopy(children,  index, newc, index + 1, children.length - index);
		this.children = newc;
	}
	private Node[] children;
	private byte[] letters;
	private boolean terminated;
}

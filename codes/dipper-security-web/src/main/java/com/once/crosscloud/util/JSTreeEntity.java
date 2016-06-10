/**
 * Copyright 2016-2016 Institute of Software, Chinese Academy of Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.once.crosscloud.util;

import java.io.Serializable;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 9, 2016
 */
public class JSTreeEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String parent;
	
	private String text;
	
	private String icon;
	
	private State state;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public class State
	{
		private boolean opened;
		
		private boolean selected;
		
		private boolean disabled;

		public boolean isOpened() {
			return opened;
		}

		public void setOpened(boolean opened) {
			this.opened = opened;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		@Override
		public String toString() {
			return "State [opened=" + opened + ", selected=" + selected
					+ ", disabled=" + disabled + "]";
		}
		
		
	}

	@Override
	public String toString() {
		return "JSTree [id=" + id + ", parent=" + parent + ", text=" + text
				+ ", icon=" + icon + ", state=" + state + "]";
	}
	
	
}

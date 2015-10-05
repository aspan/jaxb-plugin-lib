/*
 * MIT License
 *
 * Copyright (c) 2014 Klemm Software Consulting, Mirko Klemm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.kscs.util.plugins.xjc.base;

import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates a formatted output of plugin usage information
 *
 * @author mirko 2014-04-03
 */
public abstract class PluginUsageBuilder {
	protected static final Pattern TITLE_PATTERN = Pattern.compile("doc\\.(\\d)\\.title\\.(\\w+)");
	protected final ResourceBundle baseResourceBundle;
	protected final ResourceBundle resourceBundle;
	protected final String keyBase;
	protected boolean firstOption = true;

	protected PluginUsageBuilder(final ResourceBundle baseResourceBundle, final ResourceBundle resourceBundle) {
		this.baseResourceBundle = baseResourceBundle;
		this.resourceBundle = resourceBundle;
		this.keyBase = "usage";
	}

	public abstract PluginUsageBuilder addMain(final String optionName);

	public abstract <T> PluginUsageBuilder addOption(final Option<?> option);

	protected static String transformName(final String xmlName) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String word : xmlName.split("\\-")) {
			if (first) {
				sb.append(word);
				first = false;
			} else {
				sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
			}
		}
		return sb.toString();
	}

	protected SortedSet<Section> getSections() {
		final SortedSet<Section> sections = new TreeSet<>();
		for(final String titleKey : this.baseResourceBundle.keySet()) {
			final Matcher matcher = PluginUsageBuilder.TITLE_PATTERN.matcher(titleKey);
			if(matcher.matches()) {
				final int index = Integer.parseInt(matcher.group(1));
				final String sectionName = matcher.group(2);
				final String title = this.baseResourceBundle.getString(titleKey);
				final String bodyKey = "doc." + sectionName;
				if(!title.isEmpty() &&this.resourceBundle.containsKey(bodyKey)) {
					sections.add(new Section(index, title,  this.resourceBundle.getString(bodyKey)));
				}
			}
		}
		return sections;
	}

	protected class Section implements Comparable<Section> {
		protected final int index;
		protected final String title;
		protected final String body;

		public Section(final int index, final String title, final String body) {
			this.index = index;
			this.title = title;
			this.body = body;
		}

		@Override
		public int compareTo(final Section o) {
			return this.index > o.index ? 1 : (this.index == o.index ? 0 : -1);
		}
	}

}

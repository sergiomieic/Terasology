/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.logic.console;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to provide the name and possibly the delimiter of a Command's parameters
 *
 * @author Immortius, Limeth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CommandParam {

    String value();

	/**
	 * Used if the command method parameter is an array.
	 * The {@code \} (backslash) character is used for escaping the delimiter.
	 * The space character can be used only when the parameter is the last one as a {@code varargs} parameter.
	 *
	 * @return The array delimiter
	 */
	char arrayDelimiter() default Command.ARRAY_DELIMITER_DEFAULT;
}

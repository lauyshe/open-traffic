/**
 * Copyright 2012. The Regents of the University of California (Regents).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package core

import org.junit._
import org.junit.Assert._
import org.joda.time.Duration
import core_extensions.MMLogging

class TimeTest extends MMLogging {

  @Test
  def test1(): Unit = {
    val t = new Time
    val dt = t.toDateTime()
    val t2 = Time.from(dt)
    assert(t2 == t)
  }

}
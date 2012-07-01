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

package core.storage
import core.GeoMultiLine
import edu.berkeley.cs.avro.marker.AvroRecord

case class GeoMultiLineRepr(
    var points: Seq[CoordinateRepresentation]) extends AvroRecord {
}

object GeoMultiLineRepr {
  def fromRepr(gr: GeoMultiLineRepr): GeoMultiLine = {
    new GeoMultiLine(gr.points.map(CoordinateRepresentation.fromRepr _).toArray)
  }

  def toRepr(g: GeoMultiLine): GeoMultiLineRepr = {
    new GeoMultiLineRepr(g.getCoordinates().map(CoordinateRepresentation.toRepr _))
  }
}
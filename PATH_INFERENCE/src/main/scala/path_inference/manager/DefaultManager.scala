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

package path_inference.manager

import collection.mutable.HashMap
import collection.mutable.Queue
import core_extensions.MMLogging
import netconfig.Datum.ProbeCoordinate
import netconfig.Link
import path_inference.models.ObservationModel
import path_inference.models.TransitionModel
import path_inference.shortest_path.PathGenerator2
import path_inference.PathInferenceParameters2
import path_inference.VehicleFilter3
import path_inference.VehicleFilter
import java.util.concurrent.atomic.AtomicInteger

class DefaultManager(
    val parameters: PathInferenceParameters2,
  val obs_model: ObservationModel,
  val trans_model: TransitionModel,
  val common_path_discovery: PathGenerator2,
  val projection_hook: ProjectionHookInterface)
  extends PathInferenceManager with MMLogging {

  // Check the parameters here, will throw an exception if invalid.
  parameters.assertValidParameters

  /**
   * Filter for the individual vehicles.
   */
  private val v_filters = HashMap.empty[String, VehicleFilter3]

  val internal_storage = new InternalStorage(parameters)

  val active_trackers_counter = new AtomicInteger(0)

  override def addPoint(point: ProbeCoordinate[Link]): Unit = synchronized {
    val id = point.id
    v_filters.get(id) match {
      case None =>
        logInfo("creating new tracker for id " + id)
        val filter = VehicleFilter.createVehicleFilter(parameters, point, obs_model, trans_model, internal_storage, projection_hook)
        v_filters += id -> filter
      // No need to add the point, it is already included in the constructor.
      case Some(filter) =>
        //        logInfo("Adding point to tracker\n"+point)
        filter addProbeCoordinate point
      // No need to check the output of the filter
      // It will be automatically sent to the internal storage object.
      // Check how recent the point is and discard too old trackers
    }
  }

  override def getProbeCoordinates = internal_storage.getProbeCoordinates

  override def getPathInferences = internal_storage.getPathInferences

  override def getRouteTTs = internal_storage.getRouteTTs

//  override def getTrajectories = internal_storage.getTrajectories

  override def getTSpots = internal_storage.getTSpots

  def finalizeManager: Unit = {
    // Tell all the filters to finalize their computations
    for (filter <- v_filters.values)
      filter.finalizeTracker
    // Discard all the filters, since we are done with them.
    v_filters.clear
    // Make sure the cache is flushed to the disk, if necessary.
    common_path_discovery.finalizeOperations
  }
  internal_storage.finalizeComputations
}
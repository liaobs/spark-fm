package de.kp.spark.fm.source
/* Copyright (c) 2014 Dr. Krusche & Partner PartG
* 
* This file is part of the Spark-Outlier project
* (https://github.com/skrusche63/spark-outlier).
* 
* Spark-Outlier is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* Spark-Outlier is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* Spark-Outlier. 
* 
* If not, see <http://www.gnu.org/licenses/>.
*/

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import de.kp.spark.fm.SparseVector
import de.kp.spark.fm.model._

/**
 * EventSource is a higher level abstraction layer and accepts customer engagement events,
 * that have been transformed into user-item ratings with additional contexual information.
 * 
 * These event-based rating data are generated by spark-pref (see this project on github)
 */
class EventSource(@transient sc:SparkContext) {

  private val model = new EventModel(sc)
  
  def get(data:Map[String,String]):RDD[(Int,(Double,SparseVector))] = {
        
    val uid = data("uid")
    val partitions = data("num_partitions").toInt

    val source = data("source")
    source match {

      case Sources.FILE => {
        
        val rawset = new FileSource(sc).connect(data)
        model.buildFile(uid,rawset,partitions)
        
      }

      case Sources.ELASTIC => {
       
       val rawset = new ElasticSource(sc).connect(data)
       model.buildElastic(uid,rawset,partitions)
       
      }
 
      case Sources.JDBC => {
        
        val rawset = new JdbcSource(sc).connect(data)
        model.buildJDBC(uid,rawset,partitions)

      }
      
      case _ => null
      
    }

  }
  

}
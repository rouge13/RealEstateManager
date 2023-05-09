package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface PropertyDao {
    // Get all properties
    @Query("SELECT * FROM property ORDER BY id ASC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(property: PropertyEntity)

    // Get all properties filtered
//    @Query("SELECT * FROM property WHERE typeOfHouse IN (:typesOfHouses) IN (:agentsId) IN (:city) IN (:boroughs) ORDER BY id ASC")
//    fun getFilteredProperties(typesOfHouses: List<String>, agentsId: List<Int>, city: List<String>, boroughs: List<String>): Flow<List<PropertyEntity>>
//
//    //In SQL, you can use the `WHERE` clause to filter the rows returned by a query based on a certain condition.
//    //
//    //If you want to retrieve rows where a particular column `x` is equal to a certain value or where the value is `NULL`, you can use the `IS NULL` operator.
//    //
//    //The syntax for such a query would be:
//    //
//    //```
//    //SELECT *
//    //FROM your_table
//    //WHERE x = 'value' OR x IS NULL;
//    //```
//    //
//    //This will return all rows where the value in column `x` is equal to `'value'` or where it is `NULL`.
//    //
//    //Note that you cannot use the `=` operator to compare a value to `NULL`, as `NULL` is not a value but rather a lack of a value. You must use the `IS NULL` operator instead.
    // Get all properties filtered
    @Query(
        """
    SELECT property.* FROM property
    INNER JOIN address ON property.id = address.propertyId
    INNER JOIN agent ON property.agentId = agent.id
    WHERE (1 = 1)
    AND (
        CASE
            WHEN (:typesOfHousesCount > 0) THEN typeOfHouse IN (:typesOfHouses)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:agentsIdCount > 0) THEN agentId IN (:agentsId)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:cityCount > 0) THEN city IN (:city)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:boroughsCount > 0) THEN boroughs IN (:boroughs)
            ELSE 1
        END
    )
    AND (:minPrice IS NULL OR price >= :minPrice)
    AND (:maxPrice IS NULL OR price <= :maxPrice)
    AND (:minSquareFeet IS NULL OR squareFeet >= :minSquareFeet)
    AND (:maxSquareFeet IS NULL OR squareFeet <= :maxSquareFeet)
    AND (:minCountRooms IS NULL OR roomsCount >= :minCountRooms)
    AND (:maxCountRooms IS NULL OR roomsCount <= :maxCountRooms)
    AND (:minCountBedrooms IS NULL OR bedroomsCount >= :minCountBedrooms)
    AND (:maxCountBedrooms IS NULL OR bedroomsCount <= :maxCountBedrooms)
    AND (:minCountBathrooms IS NULL OR bathroomsCount >= :minCountBathrooms)
    AND (:maxCountBathrooms IS NULL OR bathroomsCount <= :maxCountBathrooms)
    AND (
    CASE
        WHEN (:isSold IS NOT NULL AND :isSold) THEN 
            ((:startDate IS NULL OR dateSold >= :startDate) AND (:endDate IS NULL OR dateSold <= :endDate))
        WHEN (:isSold IS NOT NULL AND NOT :isSold) THEN 
            ((:startDate IS NULL OR dateStartSelling >= :startDate) AND (:endDate IS NULL OR dateStartSelling <= :endDate))
        ELSE 1
    END
)
    AND (
        CASE
            WHEN :isSold IS NULL THEN 1
            ELSE isSold = :isSold
        END
    )
    AND (
        CASE
            WHEN (:schoolProximity IS NOT NULL) THEN (schoolProximity = :schoolProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:shopProximity IS NOT NULL) THEN (shoppingProximity = :shopProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:parkProximity IS NOT NULL) THEN (parkProximity = :parkProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:restaurantProximity IS NOT NULL) THEN (restaurantProximity = :restaurantProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:publicTransportProximity IS NOT NULL) THEN (publicTransportProximity = :publicTransportProximity)
            ELSE 1
        END
    )
    AND property.id IN (SELECT propertyId FROM photo GROUP BY propertyId HAVING (:minCountPhotos IS NULL OR COUNT(id) >= :minCountPhotos) AND (:maxCountPhotos IS NULL OR COUNT(id) <= :maxCountPhotos))
    ORDER BY property.id ASC
"""
    )
    fun getAllFilteredProperties(
        typesOfHouses: List<String>,
        agentsId: List<Int>,
        city: List<String>,
        boroughs: List<String>,
        minPrice: Int?,
        maxPrice: Int?,
        minSquareFeet: Int?,
        maxSquareFeet: Int?,
        minCountRooms: Int?,
        maxCountRooms: Int?,
        minCountBedrooms: Int?,
        maxCountBedrooms: Int?,
        minCountBathrooms: Int?,
        maxCountBathrooms: Int?,
        minCountPhotos: Int?,
        maxCountPhotos: Int?,
        startDate: Long?,
        endDate: Long?,
        isSold: Boolean?,
        schoolProximity: Boolean?,
        shopProximity: Boolean?,
        parkProximity: Boolean?,
        restaurantProximity: Boolean?,
        publicTransportProximity: Boolean?,
        typesOfHousesCount: Int,
        agentsIdCount: Int,
        cityCount: Int,
        boroughsCount: Int
    ): Flow<List<PropertyEntity>>

}
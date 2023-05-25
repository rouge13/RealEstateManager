package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyRepository(private val propertyDao: PropertyDao) {
    // Get all the properties from the database
    val allProperties: Flow<List<PropertyEntity>> = propertyDao.getAllProperties()
    suspend fun insert(property: PropertyEntity): Long? {
        val id = propertyDao.insert(property)
        return if (id != -1L) id else null
    }

    // Set and return all Filtered properties
    fun setFilteredProperties(searchCriteria: SearchCriteria): Flow<List<PropertyEntity>> {
        return propertyDao.getAllFilteredProperties(
            typesOfHouses = searchCriteria.selectedTypeOfHouseForQuery,
            agentsId = searchCriteria.selectedAgentsIdsForQuery,
            city = searchCriteria.selectedCitiesForQuery,
            boroughs = searchCriteria.selectedBoroughsForQuery,
            minPrice = searchCriteria.selectedMinPriceForQuery,
            maxPrice = searchCriteria.selectedMaxPriceForQuery,
            minSquareFeet = searchCriteria.selectedMinSquareFeetForQuery,
            maxSquareFeet = searchCriteria.selectedMaxSquareFeetForQuery,
            minCountRooms = searchCriteria.selectedMinCountRoomsForQuery,
            maxCountRooms = searchCriteria.selectedMaxCountRoomsForQuery,
            minCountBedrooms = searchCriteria.selectedMinCountBedroomsForQuery,
            maxCountBedrooms = searchCriteria.selectedMaxCountBedroomsForQuery,
            minCountBathrooms = searchCriteria.selectedMinCountBathroomsForQuery,
            maxCountBathrooms = searchCriteria.selectedMaxCountBathroomsForQuery,
            minCountPhotos = searchCriteria.selectedMinCountPhotosForQuery,
            maxCountPhotos = searchCriteria.selectedMaxCountPhotosForQuery,
            startDate = searchCriteria.selectedStartDateForQuery,
            endDate = searchCriteria.selectedEndDateForQuery,
            isSold = searchCriteria.selectedIsSoldForQuery,
            schoolProximity = searchCriteria.selectedSchoolProximityQuery,
            shopProximity = searchCriteria.selectedShopProximityQuery,
            parkProximity = searchCriteria.selectedParkProximityQuery,
            restaurantProximity = searchCriteria.selectedRestaurantProximityQuery,
            publicTransportProximity = searchCriteria.selectedPublicTransportProximityQuery,
            typesOfHousesCount = searchCriteria.selectedTypeOfHouseForQuery.size,
            agentsIdCount = searchCriteria.selectedAgentsIdsForQuery.size,
            cityCount = searchCriteria.selectedCitiesForQuery.size,
            boroughsCount = searchCriteria.selectedBoroughsForQuery.size
        )
    }
    // Update the property
    suspend fun update(property: PropertyEntity) {
        property.id?.let {
            property.agentId?.let { it1 ->
                propertyDao.updateProperty(
                    it,
                    property.price,
                    property.squareFeet,
                    property.roomsCount,
                    property.bedroomsCount,
                    property.bathroomsCount,
                    property.description,
                    property.typeOfHouse,
                    property.isSold,
                    property.dateStartSelling,
                    property.dateSold,
                    it1,
                    property.primaryPhoto,
                    property.schoolProximity,
                    property.shoppingProximity,
                    property.parkProximity,
                    property.restaurantProximity,
                    property.publicTransportProximity,
                )
            }
        }
    }
}

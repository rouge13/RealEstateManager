package com.openclassrooms.realestatemanager.data.model

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
data class SearchCriteria(
    var selectedAgentsIdsForQuery: List<Int> = emptyList(),
    var selectedTypeOfHouseForQuery: List<String> = emptyList(),
    var selectedBoroughsForQuery: List<String> = emptyList(),
    var selectedCitiesForQuery: List<String> = emptyList(),
    var selectedMinPriceForQuery: Int? = null,
    var selectedMaxPriceForQuery: Int? = null,
    var selectedMinSquareFeetForQuery: Int? = null,
    var selectedMaxSquareFeetForQuery: Int? = null,
    var selectedMinCountRoomsForQuery: Int? = null,
    var selectedMaxCountRoomsForQuery: Int? = null,
    var selectedMinCountBedroomsForQuery: Int? = null,
    var selectedMaxCountBedroomsForQuery: Int? = null,
    var selectedMinCountBathroomsForQuery: Int? = null,
    var selectedMaxCountBathroomsForQuery: Int? = null,
    var selectedMinCountPhotosForQuery: Int? = null,
    var selectedMaxCountPhotosForQuery: Int? = null,
    var selectedStartDateForQuery: String? = null,
    var selectedEndDateForQuery: String? = null,
    var selectedIsSoldForQuery: Boolean = false,
    var selectedSchoolProximityQuery: Boolean? = null,
    var selectedShopProximityQuery: Boolean? = null,
    var selectedParkProximityQuery: Boolean? = null,
    var selectedRestaurantProximityQuery: Boolean? = null,
    var selectedPublicTransportProximityQuery: Boolean? = null
) {

}
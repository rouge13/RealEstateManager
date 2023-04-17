package com.openclassrooms.realestatemanager.data.database

import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.ProximityEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class FixturesDatas {
    companion object {
        val AGENT_LIST = listOf(
            AgentEntity(1, "Julien", "Hammer", "jhammer@gmail.com", "ic_agent_1", "1234"),
            AgentEntity(2, "Carl", "Smith", "csmith@gmail.com", "ic_agent_2jha", "1234")
        )

        val PROPERTY_PROXIMITY_LIST = listOf(
            ProximityEntity(1, true, true, true, true, true, 1),
            ProximityEntity(2, false, false, false, false, false, 2),
            ProximityEntity(3, false, true, false, false, true, 3),
            ProximityEntity(4, false, false, true, false, false, 4),
            ProximityEntity(5, false, true, false, true, false, 5),
            ProximityEntity(6, true, false, false, true, true, 6)
        )

        val PROPERTY_ADDRESS_LIST = listOf(
            AddressEntity(1,  "345", "Park Ave", "New York", "Manhattan", "10154", "USA", 40.759011, -73.969111, 1),
            AddressEntity(2, "160", "Schermerhorn St", "Brooklyn", "Brooklyn", "11201", "USA", 40.689256, -73.9874257, 2),
            AddressEntity(3, "29-10", "Thomson Ave", "Long Island City", "Queens", "11101", "USA", 40.745234, -73.937686, 3),
            AddressEntity(4, "1", "Edgewater Plaza", "Staten Island", "Staten Island", "10305", "USA", 40.619287, -74.068194, 4),
            AddressEntity(5, "126-02", "82nd Ave", "Kew Gardens", "Queens", "11415", "USA", 40.713266, -73.825877, 5),
            AddressEntity(6, "174th", "St & Grand Concourse", "Bronx", "Bronx", "10457", "USA", 40.837445, -73.887809, 6)
        )

        val PROPERTY_LIST = listOf(
            PropertyEntity(
                1,
                17000000,
                250,
                10,
                2,
                2,
                "A flat, also known as an apartment, is a self-contained housing unit that occupies only part of a building. In the United States, flats are typically rented rather than owned, although it is possible to buy a flat in some areas. Flats can vary in size and layout, but they generally include a living area, one or more bedrooms, a bathroom, and a kitchen. Some flats may also have additional amenities, such as a balcony, a laundry room, or a swimming pool. The cost of renting a flat in the US can vary widely depending on the location, size, and quality of the unit.",
                "Flat",
                false,
                "09/04/2023",
                "",
                1,
                "ic_flat_house1",
                1681057947516
            ),
            PropertyEntity(
                2,
                25000000,
                300,
                14,
                3,
                3,
                "A house typically refers to a physical structure that is used as a dwelling. It is often associated with the idea of property ownership and can be used to describe a variety of different types of residential buildings, such as single-family homes, townhouses, and apartments.",
                "House",
                false,
                "10/04/2023",
                "",
                2,
                "ic_house_classic1",
                1681058041885
            ),
            PropertyEntity(
                3,
                300000000,
                3000,
                25,
                6,
                6,
                "In terms of design, duplex homes can vary widely depending on the preferences of the builder and the needs of the occupants. Some duplexes may be designed to look like a single-family home from the outside, while others may have a more modern or contemporary appearance. The interior layout of each unit may also differ, with some featuring open floor plans and others having more traditional room divisions.",
                "Duplex",
                false,
                "12/04/2023",
                "",
                1,
                "ic_duplex_house1",
                1681058941044
            ),
            PropertyEntity(
                4,
                350000000,
                3500,
                22,
                8,
                6,
                "A penthouse is a luxurious apartment or condominium unit that is typically located on the top floor of a high-rise building. These types of homes are often associated with luxury living and can offer spectacular views of the surrounding area.",
                "Penthouse",
                false,
                "15/04/2023",
                "",
                2,
                "ic_penthouse_house1",
                1681058511034
            ),
            PropertyEntity(
                5,
                23000000,
                320,
                14,
                4,
                4,
                "A house typically refers to a physical structure that is used as a dwelling. It is often associated with the idea of property ownership and can be used to describe a variety of different types of residential buildings, such as single-family homes, townhouses, and apartments.",
                "House",
                false,
                "20/04/2023",
                "",
                1,
                "ic_house_classic2",
                1681058041885
            ),
            PropertyEntity(
                6,
                24000000,
                370,
                9,
                3,
                3,
                "A house typically refers to a physical structure that is used as a dwelling. It is often associated with the idea of property ownership and can be used to describe a variety of different types of residential buildings, such as single-family homes, townhouses, and apartments.",
                "House",
                false,
                "10/04/2023",
                "",
                1,
                "ic_house_classic3",
                1681058918711
            )
        )
    }
}
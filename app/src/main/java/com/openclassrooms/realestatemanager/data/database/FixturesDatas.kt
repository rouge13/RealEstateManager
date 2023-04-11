package com.openclassrooms.realestatemanager.data.database

import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class FixturesDatas {
    companion object {
        val AGENT_LIST = listOf(
            AgentEntity(1, "Julien", "Hammer", "jhammer@gmail.com", "1234"),
            AgentEntity(2, "Carl", "Smith", "csmith@gmail.com", "1234")
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
                "345 Park Ave, New York, NY 10154",
                "Manhattan",
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
                "160 Schermerhorn St, Brooklyn, NY 11201",
                "Brooklyn",
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
                "29-10 Thomson Ave, Long Island City, NY 11101",
                "Queens",
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
                "1 Edgewater Plaza, Staten Island, NY 10305",
                "Staten Island",
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
                "126-02 82nd Ave, Kew Gardens, NY 11415",
                "Queens",
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
                "174th St & Grand Concourse, Bronx, NY 10457",
                "Bronx",
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
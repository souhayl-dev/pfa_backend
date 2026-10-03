-- Development photos for the seed listings and units, so the catalogue does not open on empty cards.
--
-- They are not copied into the project: each url points at the file on Wikimedia Commons. They are
-- under free licences that ask for the author to be credited, so replace them, or credit them on
-- the page, before showing them outside development. The files, in the order they are inserted:
--
--   Riad Maroc 031.JPG (CC BY-SA 3.0)
--   Riad Cinnamon patio.jpg (CC BY-SA 3.0)
--   Riad Cinnamon roof terrace at night.jpg (CC BY-SA 3.0)
--   Corniche Casablanca (51558746).jpeg (CC BY 3.0)
--   Corniche, Casablanca.jpg (CC BY-SA 4.0)
--   Promenade sur la Corniche Ain Diab à Casablanca - photo Bertrand SOUBEYRAND.jpg (CC BY-SA 4.0)
--   2018 Dacia Duster Comfort 1.6.jpg (CC BY-SA 4.0)
--   Marrakesh Menara Airport New Terminal.jpg (CC BY-SA 4.0)
--   2018 Dacia Duster.jpg (CC BY-SA 4.0)
--   Renault Clio V 1X7A0392.jpg (CC BY-SA 4.0)
--   Boulevard Mohammed V, Casablanca.jpg (CC0)
--   Renault Clio V (2023) 1X7A1577.jpg (CC BY-SA 4.0)
--   Moroccan tajine with meat.jpg (CC BY-SA 4.0)
--   Couscous-1.jpg (CC BY-SA 3.0)
--   Moroccan tajine.jpg (CC BY-SA 4.0)
--   Jebel Toubkal and a group of hikers.jpg (CC BY-SA 4.0)
--   Trekking Toubkal 2019.jpg (CC BY-SA 4.0)
--   Imlil village, High Atlas Mountains.jpg (CC BY-SA 4.0)
--   Ksar Aït Benhaddou, Marocco (أيت بن حدو، المغرب, ⴰⵢⵜ ⵃⴰⴷⴷⵓ).jpg (CC BY-SA 4.0)
--   Tizi n'Tichka.jpg (CC BY-SA 4.0)
--   Ouzoud waterfalls In spring-Morocco.jpg (CC BY-SA 4.0)
--   At Morocco 2023 33.jpg (CC BY-SA 4.0)
--   Morocco Maroc - Marrakech - Riad Houdou - Photo Image Photography (9127830044).jpg (CC BY 2.0)
--   Grand Hyatt Taipei bedroom in Grand Premier Room.JPG (CC BY-SA 4.0)
--   2018 Dacia Duster Laureate DCi Automatic 1.5.jpg (CC BY-SA 4.0)
--   Renault Clio V 1X7A1979.jpg (CC BY-SA 4.0)
--   Moroccan Tajine with peas and olives.jpg (CC BY-SA 2.0)
--   Tizi n Toubkal(1).jpg (CC BY-SA 4.0)
--   TRANSPORT TOURISTIQUES MERCEDES MINIBUS IN THE ATLAS MOUNTAINS MOROCCO APRIL 2013 (8740040573).jpg (CC BY-SA 2.0)
--   Aït Benhaddou view from above.jpg (CC BY-SA 4.0)

insert into photos (id, listing_id, unit_id, url, sort_order, created_at) values
    -- Riad Jardin Secret
    ('40000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/7/72/Riad_Maroc_031.JPG/1280px-Riad_Maroc_031.JPG', 0, now(6)),
    ('40000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/2/22/Riad_Cinnamon_patio.jpg/1280px-Riad_Cinnamon_patio.jpg', 1, now(6)),
    ('40000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000001', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/c/cd/Riad_Cinnamon_roof_terrace_at_night.jpg/1280px-Riad_Cinnamon_roof_terrace_at_night.jpg', 2, now(6)),
    -- Hotel Atlantic Casablanca
    ('40000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000002', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/f/fa/Corniche_Casablanca_%2851558746%29.jpeg/1280px-Corniche_Casablanca_%2851558746%29.jpeg', 0, now(6)),
    ('40000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000002', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/4/4d/Corniche%2C_Casablanca.jpg/1280px-Corniche%2C_Casablanca.jpg', 1, now(6)),
    ('40000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000002', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/f/f7/Promenade_sur_la_Corniche_Ain_Diab_%C3%A0_Casablanca_-_photo_Bertrand_SOUBEYRAND.jpg/1280px-Promenade_sur_la_Corniche_Ain_Diab_%C3%A0_Casablanca_-_photo_Bertrand_SOUBEYRAND.jpg', 2, now(6)),
    -- Youssef Location - Marrakech Airport
    ('40000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000003', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/c/c2/2018_Dacia_Duster_Comfort_1.6.jpg/1280px-2018_Dacia_Duster_Comfort_1.6.jpg', 0, now(6)),
    ('40000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000003', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/0/05/Marrakesh_Menara_Airport_New_Terminal.jpg/1280px-Marrakesh_Menara_Airport_New_Terminal.jpg', 1, now(6)),
    ('40000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000003', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/c/c9/2018_Dacia_Duster.jpg/1280px-2018_Dacia_Duster.jpg', 2, now(6)),
    -- Youssef Location - Casablanca Centre
    ('40000000-0000-0000-0000-000000000010', '20000000-0000-0000-0000-000000000004', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/1/1c/Renault_Clio_V_1X7A0392.jpg/1280px-Renault_Clio_V_1X7A0392.jpg', 0, now(6)),
    ('40000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000004', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/2/2a/Boulevard_Mohammed_V%2C_Casablanca.jpg/1280px-Boulevard_Mohammed_V%2C_Casablanca.jpg', 1, now(6)),
    ('40000000-0000-0000-0000-000000000012', '20000000-0000-0000-0000-000000000004', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/c/c6/Renault_Clio_V_%282023%29_1X7A1577.jpg/1280px-Renault_Clio_V_%282023%29_1X7A1577.jpg', 2, now(6)),
    -- La Terrasse des Epices
    ('40000000-0000-0000-0000-000000000013', '20000000-0000-0000-0000-000000000005', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/5/5a/Moroccan_tajine_with_meat.jpg/1280px-Moroccan_tajine_with_meat.jpg', 0, now(6)),
    ('40000000-0000-0000-0000-000000000014', '20000000-0000-0000-0000-000000000005', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/a4/Couscous-1.jpg/1280px-Couscous-1.jpg', 1, now(6)),
    ('40000000-0000-0000-0000-000000000015', '20000000-0000-0000-0000-000000000005', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/0/09/Moroccan_tajine.jpg/1280px-Moroccan_tajine.jpg', 2, now(6)),
    -- Fatima - Atlas Mountain Guide
    ('40000000-0000-0000-0000-000000000016', '20000000-0000-0000-0000-000000000006', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/5/56/Jebel_Toubkal_and_a_group_of_hikers.jpg/1280px-Jebel_Toubkal_and_a_group_of_hikers.jpg', 0, now(6)),
    ('40000000-0000-0000-0000-000000000017', '20000000-0000-0000-0000-000000000006', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/5/57/Trekking_Toubkal_2019.jpg/1280px-Trekking_Toubkal_2019.jpg', 1, now(6)),
    ('40000000-0000-0000-0000-000000000018', '20000000-0000-0000-0000-000000000006', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/0/04/Imlil_village%2C_High_Atlas_Mountains.jpg/1280px-Imlil_village%2C_High_Atlas_Mountains.jpg', 2, now(6)),
    -- Fatima Excursions
    ('40000000-0000-0000-0000-000000000019', '20000000-0000-0000-0000-000000000007', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/d/d5/Ksar_A%C3%AFt_Benhaddou%2C_Marocco_%28%D8%A3%D9%8A%D8%AA_%D8%A8%D9%86_%D8%AD%D8%AF%D9%88%D8%8C_%D8%A7%D9%84%D9%85%D8%BA%D8%B1%D8%A8%2C_%E2%B4%B0%E2%B5%A2%E2%B5%9C_%E2%B5%83%E2%B4%B0%E2%B4%B7%E2%B4%B7%E2%B5%93%29.jpg/1280px-Ksar_A%C3%AFt_Benhaddou%2C_Marocco_%28%D8%A3%D9%8A%D8%AA_%D8%A8%D9%86_%D8%AD%D8%AF%D9%88%D8%8C_%D8%A7%D9%84%D9%85%D8%BA%D8%B1%D8%A8%2C_%E2%B4%B0%E2%B5%A2%E2%B5%9C_%E2%B5%83%E2%B4%B0%E2%B4%B7%E2%B4%B7%E2%B5%93%29.jpg', 0, now(6)),
    ('40000000-0000-0000-0000-000000000020', '20000000-0000-0000-0000-000000000007', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/3/3d/Tizi_n%27Tichka.jpg/1280px-Tizi_n%27Tichka.jpg', 1, now(6)),
    ('40000000-0000-0000-0000-000000000021', '20000000-0000-0000-0000-000000000007', null,
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/7/76/Ouzoud_waterfalls_In_spring-Morocco.jpg/1280px-Ouzoud_waterfalls_In_spring-Morocco.jpg', 2, now(6)),
    -- Patio Double Room
    ('40000000-0000-0000-0000-000000000022', null, '30000000-0000-0000-0000-000000000001',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e8/At_Morocco_2023_33.jpg/1280px-At_Morocco_2023_33.jpg', 0, now(6)),
    -- Terrace Suite
    ('40000000-0000-0000-0000-000000000023', null, '30000000-0000-0000-0000-000000000002',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e2/Morocco_Maroc_-_Marrakech_-_Riad_Houdou_-_Photo_Image_Photography_%289127830044%29.jpg/1280px-Morocco_Maroc_-_Marrakech_-_Riad_Houdou_-_Photo_Image_Photography_%289127830044%29.jpg', 0, now(6)),
    -- Sea View Double Room
    ('40000000-0000-0000-0000-000000000024', null, '30000000-0000-0000-0000-000000000003',
     'https://upload.wikimedia.org/wikipedia/commons/1/10/Grand_Hyatt_Taipei_bedroom_in_Grand_Premier_Room.JPG', 0, now(6)),
    -- Dacia Duster Automatic
    ('40000000-0000-0000-0000-000000000025', null, '30000000-0000-0000-0000-000000000004',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/1/1f/2018_Dacia_Duster_Laureate_DCi_Automatic_1.5.jpg/1280px-2018_Dacia_Duster_Laureate_DCi_Automatic_1.5.jpg', 0, now(6)),
    -- Renault Clio Manual
    ('40000000-0000-0000-0000-000000000026', null, '30000000-0000-0000-0000-000000000005',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/1/13/Renault_Clio_V_1X7A1979.jpg/1280px-Renault_Clio_V_1X7A1979.jpg', 0, now(6)),
    -- Terrace seating
    ('40000000-0000-0000-0000-000000000027', null, '30000000-0000-0000-0000-000000000006',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/4/45/Moroccan_Tajine_with_peas_and_olives.jpg/1280px-Moroccan_Tajine_with_peas_and_olives.jpg', 0, now(6)),
    -- Full-day Atlas hike
    ('40000000-0000-0000-0000-000000000028', null, '30000000-0000-0000-0000-000000000007',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/c/c5/Tizi_n_Toubkal%281%29.jpg/1280px-Tizi_n_Toubkal%281%29.jpg', 0, now(6)),
    -- Marrakech airport transfer
    ('40000000-0000-0000-0000-000000000029', null, '30000000-0000-0000-0000-000000000008',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/d/d3/TRANSPORT_TOURISTIQUES_MERCEDES_MINIBUS_IN_THE_ATLAS_MOUNTAINS_MOROCCO_APRIL_2013_%288740040573%29.jpg/1280px-TRANSPORT_TOURISTIQUES_MERCEDES_MINIBUS_IN_THE_ATLAS_MOUNTAINS_MOROCCO_APRIL_2013_%288740040573%29.jpg', 0, now(6)),
    -- Atlas and Valleys
    ('40000000-0000-0000-0000-000000000030', null, '30000000-0000-0000-0000-000000000009',
     'https://thumb.wikimedia.org/wikipedia/commons/thumb/8/83/A%C3%AFt_Benhaddou_view_from_above.jpg/1280px-A%C3%AFt_Benhaddou_view_from_above.jpg', 0, now(6));

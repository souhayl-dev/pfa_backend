-- Better photos for Hotel Atlantic Casablanca: the first three did not show the place well.
-- Like the others, they are files on Wikimedia Commons under licences that ask for credit:
--
--   Sunshine on mosque Hassan II in Casablanca, Morocco - Flickr - Milamber's portfolio.jpg (CC BY 2.0)
--   Boulevard de la Corniche, Dar-el-Beida, Morocco - panoramio (14).jpg (CC BY-SA 3.0)
--   Hassan II Mosque Plaza.jpg (CC BY-SA 4.0)

update photos set url = 'https://thumb.wikimedia.org/wikipedia/commons/thumb/8/8a/Sunshine_on_mosque_Hassan_II_in_Casablanca%2C_Morocco_-_Flickr_-_Milamber%27s_portfolio.jpg/1280px-Sunshine_on_mosque_Hassan_II_in_Casablanca%2C_Morocco_-_Flickr_-_Milamber%27s_portfolio.jpg'
 where id = '40000000-0000-0000-0000-000000000004';
update photos set url = 'https://thumb.wikimedia.org/wikipedia/commons/thumb/8/8a/Boulevard_de_la_Corniche%2C_Dar-el-Beida%2C_Morocco_-_panoramio_%2814%29.jpg/1280px-Boulevard_de_la_Corniche%2C_Dar-el-Beida%2C_Morocco_-_panoramio_%2814%29.jpg'
 where id = '40000000-0000-0000-0000-000000000005';
update photos set url = 'https://thumb.wikimedia.org/wikipedia/commons/thumb/3/32/Hassan_II_Mosque_Plaza.jpg/1280px-Hassan_II_Mosque_Plaza.jpg'
 where id = '40000000-0000-0000-0000-000000000006';

// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ISatelliteService

#if DEBUG
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Satellite.Models;

namespace PocketCampus.Satellite.Services.Design
{
    public sealed class DesignSatelliteService : ISatelliteService
    {
        public Task<BeersResponse> GetBeersAsync( CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new BeersResponse
                {
                    Status = BeerMenuStatus.Success,
                    BeerMenu = new Dictionary<BeerContainer, BeerMenuPart>
                    {
                        { BeerContainer.Draft,
                            new BeerMenuPart
                            {
                                BeersOfTheMonth = new[]
                                {
                                    new Beer
                                    {
                                        Name = "Lupulus Brune",
                                        AlcoholRate = 8.5,
                                        BreweryName = "Les 3 Fourquets",
                                        CountryName = "Belgique",
                                        Price = 13.0,
Description = @"Il s’agit d’une bière titrant 8.5 % alcool volume,
refermentée en bouteilles de ¾ l et en fûts de 20 L. La bière est très atténuée,
ce qui veut dire qu’elle est très facile à boire parce qu’elle n’est pas lourde. La coloration est due à du sucre candi. En fin d’ébullition une petite quantité  d’écorces d’oranges est ajoutée.  Selon les personnes qui l’ont déjà goûtée il s’agit d’une brune très différente des brunes traditionnelles et de goût très apprécié."
                                    }
                                },
                                Beers = new Dictionary<string, Beer[]>
                                {
                                    { "Blonde",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Gurten Lager",
                                                AlcoholRate = 4.8,
                                                BreweryName = "Gurten",
                                                CountryName = "Suisse",
                                                Price = 3.0,
Description = @"La bière désaltérante par excellence,
agréable à boire,
légère et savoureuse. Très légèrement houblonnée,
peu amère,
elle convient en toute situation. Elle est LA bière classique de notre assortiment,
idéale pour satisfaire une soif soudaine qui n'exige pas une dégustation passionnée."
                                            },
                                            new Beer
                                            {
                                                Name = "Cuvée des Trolls",
                                                AlcoholRate = 7.0,
                                                BreweryName = "Dubuisson",
                                                CountryName = "Bélgique",
                                                Price = 4.0,
Description = @"Il s'agit d'une bière blonde,
filtrée,
fraîche et parfumée,
particulièrement ronde et bien équilibrée,
et titrant 7% de volume alcool.
Elle est brassée à partir de moût sucré auquel on rajoute du houblon et des écorces d'orange séchées. Il s'agit d'une fermentation haute."
                                            },
                                            new Beer
                                            {
                                                Name = "Tempête",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Docteur Gab'S",
                                                CountryName = "Suisse",
                                                Price = 4.0,
Description = @"Depuis cet automne nous servons en pression une des bières de la brasserie locale Docteur Gab's. On alterne entre la Tempête,
la Chameau ainsi que des spécialités saisonnières.
Cette bière blonde double malt combine force de caractère et amertume subtile."
                                            }
                                        }
                                    },
                                    { "Blanche",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Schneider Weisse",
                                                AlcoholRate = 5.2,
                                                BreweryName = "G. Schneider",
                                                CountryName = "Allemagne",
                                                Price = 4.0,
Description = @"Une belle mousse,
un nez bien malté et une douceur mêlée à un malt très légèrement torréfié,
voilà une bière bien agréable,
avec un bon petit caractère."
                                            },
                                            new Beer
                                            {
                                                Name = "Wittekop",
                                                AlcoholRate = 4.5,
                                                BreweryName = "Riva",
                                                CountryName = "Belgique",
                                                Price = 3.5,
Description = @"Bière de haute fermentation,
non-filtrée,
naturellement trouble. Son arôme subtil est dût à l'adjonction d'épices et son goût piquant et fruité à un processus de fermentation particulier."
                                            }
                                        }
                                    },
                                    { "Lager",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Super Bock",
                                                AlcoholRate = 5.0,
                                                BreweryName = "Unicer",
                                                CountryName = "Portugal",
                                                Price = 3.0,
Description = @"Légèrement sucrée et fruitée (groseille),
elle se laisserait facilement passer pour un cocktail. Brassée à base de matières premières sélectionnées pour leur excellente qualité,
la Super Bock a été récompensée par 28 médailles d'Or au Concours International""Monde Sélection""."
                                            }
                                        }
                                    },
                                    { "Bière D",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Leffe Blonde",
                                                AlcoholRate = 6.6,
                                                BreweryName = "InBev",
                                                CountryName = "Belgique",
                                                Price = 4,
Description = @"Bière de haute fermentation,
brassée au malt pâle et au maïs. Goût plein,
doux et fruité. Elle une des perles des bière belges,
à la saveur surprenante et envoûtante. Bien qu'extrêmement connue et consommée,
elle reste une bière de dégustation qui se savoure à chaque instant."
                                            }
                                        }
                                    },
                                    { "Brune",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Carolus",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Het Anker",
                                                CountryName = "Bélgique",
                                                Price = 4.0,
Description = @"À l’origine,
cette bière impériale malinoise était bue principalement lors des chasses aux renards de Charles-Quint et elle a trouvé son nom dans ses pièces d'or. Composée d'un mélange soigneusement dosé de différents malts foncés,
elle est brassée suivant la méthode classique,
à savoir,
la méthode de l'infusion. Garantie de haute fermentation,
elle allie,
grâce à sa densité élevée de 19° plato,
la chaleur du vin à la fraîcheur de la bière. Elle est classée sous les bières spéciales et est particulièrement indiquée à des fins culinaires comme des étuvés,
du gibier,
des pâtés et même des sabayons."
                                            }
                                        }
                                    },
                                    { "Blonde Triple",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Karmeliet",
                                                AlcoholRate = 8.4,
                                                BreweryName = "Bosteels",
                                                CountryName = "Belgique",
                                                Price = 4.0,
Description = @"Bière de haute fermentation,
brassée à partir d'orge,
de froment et d'avoine. Au goût doux et fruité,
au saveur de citron,
d'orange ou de vanille. Elle est cependant forte,
dangereuse par son goût agréable qui cache un haut taux d'alcool."
                                            }
                                        }
                                    },
                                    { "Ambrée",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Kwak",
                                                AlcoholRate = 8.4,
                                                BreweryName = "Bosteels",
                                                CountryName = "Belgique",
                                                Price = 4.0,
Description = @"Bière de haute fermentation. Artisanale forte et corsée,
elle se boit dans un verre spécial crée à l’attention des cochers. La légende veut que son nom provienne du bruit que l’on entend quand on boit son verre cul-sec,
quand l’air remontant dans celui-ci laisse entendre le fameux «kwak». Il s’agirait en fait du nom de son inventeur,
le brasseur Pauwel Kwak."
                                            },
                                            new Beer
                                            {
                                                Name = "Kilkenny",
                                                AlcoholRate = 5.5,
                                                BreweryName = "St Francis",
                                                CountryName = "Irlande",
                                                Price = 4.0,
Description = @"Bière de haute fermentation,
à l'orge caramélisé et aux notes subtilement fruitées. Elle accompagne généralement la Guinness sur les comptoirs des pubs irlandais et vous fera découvrir le côté caché de la bière irlandaise."
                                            }
                                        }
                                    },
                                    { "Variable",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Docteur Gab's",
                                                AlcoholRate = 7.0,
                                                BreweryName = "Docteur Gab'S",
                                                CountryName = "Suisse",
                                                Price = 4.0,
Description = @"Une des tireuses du bar est exclusivement réservée aux bières de la brasserie Docteur Gab's... mais au gré des humeurs,
des envies ou des saisons,
la bière peut changer !
Venez découvrir la Tempête,
blonde puissante,
la Chameau,
ambrée au caractère complexe,
ou bien les bières saisonnières de la brasserie... Les hasards du calendrier vous permettront de découvrir en profondeur la diversité de cette excellente brasserie vaudoise !"
                                            }
                                        }
                                    },
                                    { "Stout",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Guinness",
                                                AlcoholRate = 4.2,
                                                BreweryName = "John Martin",
                                                CountryName = "Irlande",
                                                Price = 4.0,
Description = @"Bière de haute fermentation  au malt fortement torréfié et grains d'orge grillés. La plus célèbre bière irlandaise est reconnaissable à sa couleur noire et opaque,
sa mousse crémeuse et son goût authentique."
                                            }
                                        }
                                    }
                                }
                  
                            }
                        },
                        { BeerContainer.Bottle,
                            new BeerMenuPart
                            {
                                BeersOfTheMonth = new[]
                                {
                                    new Beer
                                    {
                                        Name = "Chimay Spéciale Cente Cinquante",
                                        AlcoholRate = 10.0,
                                        BreweryName = "Abbaye De Scourmont",
                                        CountryName = "Belgique",
                                        Price = 13.0,
Description = @"Développée au sein de l'abbaye à l'instar de ses sœurs,
cette bière spéciale est à la fois corsée et délicate. Produite à partir d'ingrédients 100% naturels,
elle possède une robe or pâle,
au pétillement qui rappelle celui du champagne,
et est surmontée d'une belle coiffe de mousse blanche.
Son parfum unique,
original,
évoque les notes richement fruitées et complexes de la levure de Chimay,
en harmonie avec les saveurs délicatement épicées et le parfum noble du houblon frais.
Avec une teneur en alcool de 10%,
la Chimay «Spéciale Cent Cinquante» est corsée et complexe. Son goût légèrement acidulé et rafraîchissant s'achève sur une note pétillante de houblon,
pour le plus grand plaisir du palais."
                                    },
                                    new Beer
                                            {
                                                Name = "St-Feuillien Saison",
                                                AlcoholRate = 6.5,
                                                BreweryName = "St-Feuillien",
                                                CountryName = "Belgique",
                                                Price = 4.0,
Description = @"Bière de terroir par excellence,
la Saison trouve son origine dans les fermes-brasseries du Sud de la Belgique,
et principalement en Hainaut. A l’époque,
cette bière peu alcoolisée et rafraîchissante étanchait la soif des ouvriers saisonniers.
A la Brasserie St-Feuillien,
la Saison est une bière de fermentation haute,
refermentée en bouteille,
non filtrée,
d’un chaleureux blond doré. Son profil aromatique est tout en nuances et son amertume bien marquée,
l’ensemble soutenu par une belle plénitude en bouche. Un grand classique."
                                            }
                                },
                                Beers = new Dictionary<string, Beer[]>
                                {
                                    { "Brune",
                             
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "St-Feuillien Brune",
                                                AlcoholRate = 7.5,
                                                BreweryName = "St-Feuillien",
                                                CountryName = "Belgique",
                                                Price = 6.0,
Description = @"Bière naturelle de fermentation haute,
brassée avec des malts et des houblons de premier choix. Les notes fruitées se marient harmonieusement avec la dominante réglisse et le caramel. Son arôme est riche et son goût savoureux."
                                            },
                                            new Beer
                                            {
                                                Name = "Gulden Draak",
                                                AlcoholRate = 10.5,
                                                BreweryName = "Van Steenberge",
                                                CountryName = "Belgique",
                                                Price = 5.0,
Description = @"Bière de haute fermentation,
elle est refermentée en bouteille durant minimum deux ans. Dense,
vineuse et très fruitée,
c'est une bière de fête. Certains lui distingue des saveurs de chocolat ou café en fin de bouche."
                                            },
                                            new Beer
                                            {
                                                Name = "Westmalle Dubbel",
                                                AlcoholRate = 7.0,
                                                BreweryName = "Van Westmalle",
                                                CountryName = "Belgique",
                                                Price = 5.0,
Description = @"Bière de fermentation haute au léger goût de malt. Sa saveur est à la fois riche et complexe,
fruitée et épicée,
avec une finale amère tout en fraîcheur."
                                            },
                                            new Beer
                                            {
                                                Name = "Chimay Bleue",
                                                AlcoholRate = 9.0,
                                                BreweryName = "Abbaye De Scouremont",
                                                CountryName = "Belgique",
                                                Price = 6.0,
Description = @"Bière de fermentation haute,
refermentée en bouteille et non pasteurisée. Au goût léger et et agréable de caramel,
elle est puissante et très caractéristique. Cette bière est millésimée et demande quelques années pour mûrir en bouteille."
                                            },
                                            new Beer
                                            {
                                                Name = "Maudite",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Unibroue",
                                                CountryName = "Quebec",
                                                Price = 5.0,
Description = @"Bière de haute fermentation. D'une grande douceur,
elle offre des saveurs qui évoquent le porto. C'est une bière de caractère,
grâce à la chaleur qui s'en dégage."
                                            },
                                            new Beer
                                            {
                                                Name = "Rochefort 10",
                                                AlcoholRate = 11.3,
                                                BreweryName = "Abbaye St-Remy",
                                                CountryName = "Belgique",
                                                Price = 6.0,
Description = @"Bière trappiste de haute fermentation,
à l'intensité liquoreuse remarquable. Son goût est caramélisé,
fort,
aux notes fruitées de prune mûre et de cacao. Parmi les trois Rochefort,
celle-ci est appelée la""Merveille""."
                                            },
                                            new Beer
                                            {
                                                Name = "Brugse Zot Brune",
                                                AlcoholRate = 7.5,
                                                BreweryName = "De Halve Maan",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière de fermentation haute,
refermentée en bouteille,
la Brugse Zot Dubbel est une brune belge qui possède des arômes grillés et caramélisés relevés d'un goût légèrement sucré,
contrebalancés par une légère amertume qui la rend agréable et désaltérante."
                                            },
                                            new Beer
                                            {
                                                Name = "Chameau",
                                                AlcoholRate = 7.0,
                                                BreweryName = "Docteur Gab'S",
                                                CountryName = "Suisse",
                                                Price = 6.0,
Description = @"Bière de fermentation haute,
refermentée en bouteille,
non filtrée.
Le subtil mariage de ses trois malts lui donne un corps complexe et raffiné. En finale,
l'héritage fruité de ses levures d'abbaye se prolonge par une fraîche astringence."
                                            },
                                            new Beer
                                            {
                                                Name = "Achel Brune",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Achel",
                                                CountryName = "Belgique",
                                                Price = 6.5,
Description = @"Bière de fermentation haute,
refermentée en bouteille et possédant le label trappiste,
cette Achel est une valeur sûre des brunes belges,
dont elle se démarque par une douceur et une onctuosité soulignées par des arômes fruités et légèrement sucrés."
                                            },
                                            new Beer
                                            {
                                                Name = "Trois Pistoles",
                                                AlcoholRate = 9.0,
                                                BreweryName = "Unibroue",
                                                CountryName = "Quebec",
                                                Price = 5.5,
Description = @"Bière de haute fermentation,
riche et corsée. Nez très malté aux effluves de chocolat,
de mélasse et de rhum brun. Son goût est puissant et ressemble à celui du vieux porto."
                                            }
                                        }
                                    },
                                    { "Vieille Brune",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Duchesse de Bourgogne",
                                                AlcoholRate = 6.2,
                                                BreweryName = "Verhaeghe",
                                                CountryName = "Belgique",
                                                Price = 4.0,
Description = @"Bière de haute fermentation,
mûrie en fût de chêne. Ceci lui confère un goût entre lambic et cidre,
fruitée et acide à la fois."
                                            },
                                            new Beer
                                            {
                                                Name = "Kriekbier",
                                                AlcoholRate = 6.0,
                                                BreweryName = "Liefmans",
                                                CountryName = "Belgique",
                                                Price = 6.5,
Description = @"Bière de haute fermentation. Contrairement à la plupart des kriek (lambic),
elle est produite sur une base de vielle brune,
macérée avec des cerises fraîches. Elle a un goût acide et sucré,
équilibré,
avec beaucoup de complexité. Un chef-d’oeuvre !"
                                            }
                                        }
                                    },
                                    { "Blonde",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Westmalle Tripel",
                                                AlcoholRate = 9.5,
                                                BreweryName = "Van Westmalle",
                                                CountryName = "Belgique",
                                                Price = 5.0,
Description = @"Bière de fermentation triple,
à l'amertume soutenue par les arômes de fruits. Bière complexe qui dégage des arômes fruités et un parfum de houblon aussi agréable que nuancé. Savoureuse et désaltérante à souhait."
                                            },
                                     
                                            new Beer
                                            {
                                                Name = "Budweiser Budwar",
                                                AlcoholRate = 5.0,
                                                BreweryName = "Budweiser",
                                                CountryName = "Rép. Tchèque",
                                                Price = 4.0,
Description = @"Bière à basse fermentation au goût fortement malté,
elle est peu amère,
douce et convient parfaitement au rôle de bière désaltérante. Possédant une douceur finale fruitée,
son arôme est issus de 700 ans de tradition. A ne pas confondre avec la malheureusement célèbre bière industrielle américaine."
                                            },
                                            new Beer
                                            {
                                                Name = "Desperados",
                                                AlcoholRate = 5.9,
                                                BreweryName = "Fischer",
                                                CountryName = "France",
                                                Price = 4.5,
Description = @"Bière de basse fermentation française,
à consonance mexicaine,
elle est aromatisée à la téquila. Légère,
douce,
elle se boit en toute circonstance. Un rayon de soleil sous le notre trop souvent couvert."
                                            },
                                            new Beer
                                            {
                                                Name = "Fin du Monde",
                                                AlcoholRate = 9.0,
                                                BreweryName = "Unibroue",
                                                CountryName = "Quebec",
                                                Price = 5.5,
Description = @"Bière de triple fermentation,
corsée,
maltée et qui se termine sur une note légèrement amère. Au goût épicé,
sa levure dégage un nez fruité aux effluves d'agrumes et un soupçon épicé. Elle laisse dans la bouche le souvenir d'une bière moelleuse et légèrement piquante."
                                            },
                                            new Beer
                                            {
                                                Name = "John Martin´s Pale Ale",
                                                AlcoholRate = 5.8,
                                                BreweryName = "John Martin",
                                                CountryName = "Belgique",
                                                Price = 6.0,
Description = @"Bière de haute fermentation,
fortement houblonnée et très désaltérante. D'une saveur douce et fruitée,
elle représente parfaitement la Pale Ale classique britannique."
                                            },
                                            new Beer
                                            {
                                                Name = "Duvel",
                                                AlcoholRate = 8.5,
                                                BreweryName = "Duvel",
                                                CountryName = "Belgique",
                                                Price = 5.0,
Description = @"Bière de haute fermentation,
non filtrée,
non pasteurisée,
brassée selon la méthode artisanale d'antan sans additif ni conservateur. Elle est trompeuse sous sa robe dorée anodine qui cache une force et un goût fantastique. Sa mousse est riche et onctueuse,
son arôme houblonné. Après une deuxième fermentation et une longue maturation en bouteille,
son arôme est équilibré et son amertume subtile."
                                            },
                                            new Beer
                                            {
                                                Name = "Delirium Tremens",
                                                AlcoholRate = 9.0,
                                                BreweryName = "Huyghe",
                                                CountryName = "Belgique",
                                                Price = 5.0,
Description = @"Bière de haute fermentation,
son nez puissant révèle une forte teneur en malt. Étant corsée,
forte,
avec une amertume durable,
sa saveur est étonnante.
Son nom provient du phénomène neurologique provoqué par un sevrage d'alcool chez les personnes dépendantes,
créant des hallucinations telles que le légendaire éléphant rose,
symbole de cette bière."
                                            },
                                            new Beer
                                            {
                                                Name = "Urbock",
                                                AlcoholRate = 9.6,
                                                BreweryName = "Eggenberg",
                                                CountryName = "Autriche",
                                                Price = 4.5,
Description = @"Bière de fermentation basse,
à l'arôme fortement malté. Elle possède un léger parfum de chêne et un parfum puissant."
                                            },
                                            new Beer
                                            {
                                                Name = "Carlsberg",
                                                AlcoholRate = 5.0,
                                                BreweryName = "Carlsberg",
                                                CountryName = "Danemark",
                                                Price = 3.5,
Description = @"Bière danoise de renommée internationale,
c’est une blonde de type pils,
à l’amertume franche. Appréciée des connaisseurs,
elle est relativement maltée,
dont l’arôme présente également quelques notes de pomme dues à la spécificité de sa levure."
                                            },
                                            new Beer
                                            {
                                                Name = "Tempête du Désert",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Docteur Gab'S",
                                                CountryName = "Suisse",
                                                Price = 13.0,
Description = @"Bière de fermentation haute,
refermentée en bouteille.
Lorsque la tempête s'abat sur le désert,
c'est une blonde houblonnée et toute en rondeur qui s'opère."
                                            },
                                            new Beer
                                            {
                                                Name = "Straffe Hendrik",
                                                AlcoholRate = 9.0,
                                                BreweryName = "De Halve Maan",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière de fermentation haute,
refermentée en bouteille,
la Straffe Hendrik est une bière triple belge faisant honneur à la tradition. Elle présente un bel équilibre entre puissance douceur et acidité,
laissant apparaître de subtiles notes de fruits secs."
                                            },
                                            new Beer
                                            {
                                                Name = "Achel Blonde",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Achel",
                                                CountryName = "Belgique",
                                                Price = 6.5,
Description = @"Bière de fermentation haute,
refermentée en bouteille et possédant le label trappiste,
cette Achel s'inscrit dans la pure tradition des bières triples belges. Elle séduit par son apparente simplicité et son équilibre entre une légère acidité et des notes fruitées,
la rendant plus facile et immédiate que la plupart de ses consœurs."
                                            },
                                            new Beer
                                            {
                                                Name = "Young's Special London Ale",
                                                AlcoholRate = 6.4,
                                                BreweryName = "Wells",
                                                CountryName = "Angleterre",
                                                Price = 6.5,
Description = @"Bière de fermentation haute de type""english strong ale"",
la Young's Special London Ale est un des plus fameux représentants du style ale britannique,
comme en témoignent les nombreux prix gagnés depuis sa création. Malgré sa puissance importante pour une ale britannique,
cette bière à la robe orangée est très facile à boire grâce à son corps léger et son houblonnage important conférant amertume et arômes fruités."
                                            },
                                            new Beer
                                            {
                                                Name = "Grimbergen Blonde",
                                                AlcoholRate = 6.7,
                                                BreweryName = "Abbaye De Grimbergen",
                                                CountryName = "Belgique",
                                                Price = 4.0,
Description = @"Bière de haute fermentation,
légèrement fruitée,
avec un bel équilibre entre la douceur et l’amertume. Sa saveur ample et ronde est bien prononcée.""La Grim"",
pour les intimes,
est rafraîchissante et à consommer jusqu'à plus soif."
                                            },
                                            new Beer
                                            {
                                                Name = "Orval",
                                                AlcoholRate = 6.2,
                                                BreweryName = "Orval",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière de haute fermentation,
non filtrée,
refermentée en bouteille. Authentique bière trappiste,
elle se caractérise par une amertume très prononcée. Son goût et son arôme très houblonnés lui sont propres."
                                            },
                                            new Beer
                                            {
                                                Name = "La Trappe Quadruple",
                                                AlcoholRate = 10.0,
                                                BreweryName = "Koningshoeven",
                                                CountryName = "Hollande",
                                                Price = 6.0,
Description = @"Bière de haute fermentation,
refermentée en bouteille,
brassée au malt légèrement grillé. Son goût est doux,
légèrement sucré et agréablement amer,
avec des notes de caramel.  Elle est millésimée et de bonne compagnie pour les longues soirées d’hiver."
                                            },
                                            new Beer
                                            {
                                                Name = "Corona",
                                                AlcoholRate = 4.4,
                                                BreweryName = "Cerveceria Modelo",
                                                CountryName = "Mexique",
                                                Price = 4.5,
Description = @"Bière spéciale de fermentation basse,
légère et douce,
elle a une mousse blanche peu serrée de tenue moyenne. Au nez de caramel et de malt mêlés,
sa saveur est ronde et son corps doux."
                                            }
                                        }
                                    },
                                    { "Ambrée",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Bush",
                                                AlcoholRate = 12.0,
                                                BreweryName = "Dubuisson",
                                                CountryName = "Belgique",
                                                Price = 4.5,
Description = @"Bière de haute fermentation,
filtrée,
son goût amer sucré lui donne beaucoup de caractère. C’est la plus forte bière belge et une des plus fortes au monde. Elle se caractérise par un goût et un arôme fortement malté,
contrebalancé par une amertume prononcée.
Elle peut se consommer sous forme de «café belge» lorsque combinée à une Duvel,
sensation garantie !"
                                            },
                                            new Beer
                                            {
                                                Name = "Rochefort 8",
                                                AlcoholRate = 9.2,
                                                BreweryName = "Abbaye St-Remy",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière trappiste de haute fermentation,
elle ressemble à sa consœur,
bien qu'étant plus épicée,
aux notes de caramel et de chocolat plus prononcées. Celle-ci est appleée la""Spéciale""."
                                            },
                                            new Beer
                                            {
                                                Name = "Bière du Boucanier Red Ale",
                                                AlcoholRate = 7.0,
                                                BreweryName = "Icobes B.V.B.A.",
                                                CountryName = "Belgique",
                                                Price = 5.0,
Description = @"Bière de haute fermentation brassée uniquement à partir de malt. Son goût riche et épicé lui donne un corps envoutant et complexe qui reste longtemps en bouche. Elle laisse un souvenir fort qui ne laissera personne indifférent,
ses nombreux adeptes ne pourront que le confirmer!"
                                            },
                                            new Beer
                                            {
                                                Name = "Belgoo Magus",
                                                AlcoholRate = 6.6,
                                                BreweryName = "Belgoo",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière artisanale de fermentation haute,
refermentée en bouteille et non filtrée,
la Belgoo se démarque par l'usage de 4 céréales différentes : malt d'orge,
de froment,
avoine et épeautre. Puissante mais désaltérante,
elle développe des arômes caramélisés et épicés."
                                            },
                                            new Beer
                                            {
                                                Name = "5a.m. Saint",
                                                AlcoholRate = 5.0,
                                                BreweryName = "Brewdog",
                                                CountryName = "Ecosse",
                                                Price = 5.0,
Description = @"Bière de fermentation haute,
houblonnée à cru,
cette bière est une red ale fortement houblonnée,
à la fois douce et amère à la robe rubis,
qui développe de forts arômes floraux."
                                            }
                                        }
                                    },
                                    { "Lambic",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Gueuze Foudroyante",
                                                AlcoholRate = 4.5,
                                                BreweryName = "Lindemans",
                                                CountryName = "Belgique",
                                                Price = 6.0,
Description = @"Bière à fermentation spontanée,
c'est un mélange de trois lambics d´âges différents,
refermenté en bouteille. Son nez boisé est agrémenté de touches de rhubarbe. Bière légèrement aigre au final fruité et sucré."
                                            },
                                            new Beer
                                            {
                                                Name = "Faro",
                                                AlcoholRate = 4.0,
                                                BreweryName = "Lindemans",
                                                CountryName = "Belgique",
                                                Price = 4.0,
Description = @"Bière de fermentation spontanée,
le faro est un lambic adouci par l’ajout de sucre candi brun qui lui confère un goût caramélisé. Bière douce et sucrée,
elle plaît particulièrement à la gente féminine."
                                            },
                                            new Beer
                                            {
                                                Name = "Pêcheresse",
                                                AlcoholRate = 2.5,
                                                BreweryName = "Lindemans",
                                                CountryName = "Belgique",
                                                Price = 4.5,
Description = @"Bière très sucrée,
d'où le goût est donné par une teneur d'environ 30% de jus de pêche. Fraîche et fruitée,
au riche bouquet de lambic.
Son goût très sucré et sa faible teneur en alcool lui octroient gracieusement le surnom de""Bière de Gonzesse""."
                                            }
                                        }
                                    },
                                    { "Rousse",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Chimay Rouge",
                                                AlcoholRate = 7.0,
                                                BreweryName = "Abbaye De Scouremont",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière de fermentation haute,
refermentée en bouteille et non pasterisée. Son odeur fruitée abricotée,
son équilibre et sa légère amertume en bouche rendent cette bière harmonieuse et particulièrement attrayante."
                                            }
                                        }
                                    },
                                    { "Blanche",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Andechs Weissbier",
                                                AlcoholRate = 5.5,
                                                BreweryName = "Klosterbrauerei Andechs",
                                                CountryName = "Allemagne",
                                                Price = 4.5,
Description = @"Bière de haute fermentation brassée dans les cuves ouvertes de la cave du monastère. Non-filtrée,
elle est naturellement trouble. Le goût légèrement fruité donne à cette blanche une douceur et un arôme surprenant."
                                            },
                                            new Beer
                                            {
                                                Name = "La Meule",
                                                AlcoholRate = 5.5,
                                                BreweryName = "Brasserie Des Franches-Montagnes (BFM)",
                                                CountryName = "Suisse",
                                                Price = 6.0,
Description = @"Bière de haute fermentation,
au bouquet marqué par des notes de houblons puissants (agrumes,
foin séché,
gingembre) et souligné d’une pointe de sauge. Amertume franche,
crémeuse avec une petite pointe d’acidité caractéristique des bières blanches. L’écorce d’oranges amères et un houblonnage discret sont les secrets de cette bière."
                                            },
                                            new Beer
                                            {
                                                Name = "Andechs Dunkles Weissbier",
                                                AlcoholRate = 5.0,
                                                BreweryName = "Klosterbrauerei Andechs",
                                                CountryName = "Allemagne",
                                                Price = 4.5,
Description = @"Bière de haute fermentation,
robe brune car brassée avec du malt torréfié. Son caractère aromatique la classe au rang des meilleures blanches bavaroises. Sa douceur et son arôme légèrement fruité la rendent tout simplement exceptionnelle."
                                            },
                                            new Beer
                                            {
                                                Name = "Quintine Blanche Bio",
                                                AlcoholRate = 5.9,
                                                BreweryName = "Ellezelloise",
                                                CountryName = "Belgique",
                                                Price = 6.0,
Description = @"Bière blanche de fermentation haute,
non filtrée et refermentée en bouteille,
la Quintine Bio-Organic utilise des ingrédients intégralement issus de l'agriculture biologique. Présentant des arômes citronnés et épicés,
elle est extrêmement rafraîchissante grâce à une légère amertume en fin de bouche."
                                            }
                                        }
                                    },
                                    { "Blonde Triple",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Chimay Triple",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Abbaye De Scouremont",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière de haute fermentation,
refermentée et non pasteurisée.
Son arôme vient principalement du houblon et des touches fruitées de types muscat et raisin sec. Elle possède une amertume rafraîchissante qui la rend particulièrement attrayante."
                                            }
                                        }
                                    },
                                    { "Stout",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Hercule",
                                                AlcoholRate = 9.0,
                                                BreweryName = "Ellezelloise",
                                                CountryName = "Belgique",
                                                Price = 6.0,
Description = @"Bière de haute fermentation,
non filtrée et non pasteurisée,
elle est caractéristique par sa saveur caramélisée et houblonnée. Sa mousse est abondante et son parfum malté. Elle est à la fois âpre,
aromatique,
chocolatée et reste longtemps en bouche."
                                            },
                                            new Beer
                                            {
                                                Name = "Double Chocolate",
                                                AlcoholRate = 5.2,
                                                BreweryName = "Young",
                                                CountryName = "Angleterre",
                                                Price = 5.0,
Description = @"La double bière de malt de Young de chocolat est un mélange intrigant. Le malt de chocolat et le vrai chocolat foncé sont combinés avec la richess des bière Young's."
                                            }
                                        }
                                    },
                                    { "Bière Spéciale",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Satellite Grand Cru",
                                                AlcoholRate = 8.6,
                                                BreweryName = "Les Faiseurs De Bière",
                                                CountryName = "Suisse",
                                                Price = 5.5,
Description = @"Son nom veut tout dire. Cette bière est spécialement brassée pour Satellite,
en l'honneur de ses 25 ans d'existence.
C'est un savant mélange entre la douceur d'une bière blanche et la force d'une bière ambrée.
À elle seule est représente la convivialité et la richesse de ce lieu qui nous est cher à tous."
                                            }
                                        }
                                    },
                                    { "Imperial Stout",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Cuvée Alex le Rouge",
                                                AlcoholRate = 10.2,
                                                BreweryName = "Brasserie Des Franches-Montagnes",
                                                CountryName = "Suisse",
                                                Price = 8.0,
Description = @"Bière de fermentation haute,
refermentée en bouteille et houblonnée à cru,
la cuvée Alex le Rouge est une stout riche,
aux arômes de café au lait et de vanille et au côté légèrement acidulé,
à laquelle sa belle amertume confère une longueur en bouche exceptionnelle."
                                            },
                                            new Beer
                                            {
                                                Name = "Paradox Isle of Arran",
                                                AlcoholRate = 10.0,
                                                BreweryName = "Brewdog",
                                                CountryName = "Ecosse",
                                                Price = 8.0,
Description = @"Bière de fermentation haute vieillie en fût de chêne. En plus des notes torréfiées propres aux stouts,
qui lui confèrent des arômes de café,
de fruits noirs ou de réglisse,
cette bière profite de son passage en fût de whisky de la distillerie Isle of Arran pour gagner des notes boisées et fumées ainsi qu'une magnifique substance liquoreuse.
Une expérience de dégustation unique !"
                                            }
                                        }
                                    },
                                    { "India Pale Ale",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Punk IPA",
                                                AlcoholRate = 5.6,
                                                BreweryName = "Brewdog",
                                                CountryName = "Ecosse",
                                                Price = 5.0,
Description = @"Bière de fermentation haute houblonnée à cru,
c'est le de lance de la brasserie montante Brewdog. Cette IPA dorée à l'amertume rafraîchissante est équilibrée par de persistants arômes de pêche fruits exotiques qui lui donnent sa forte personnalité."
                                            },
                                            new Beer
                                            {
                                                Name = "Hardcore IPA",
                                                AlcoholRate = 9.2,
                                                BreweryName = "Brewdog",
                                                CountryName = "Ecosse",
                                                Price = 6.0,
Description = @"Bière de fermentation haute,
la Hardcore IPA est une digne resprésentente du style""Imperial IPA"". Le principe est simple : il s'agit d'une bière de tous les excès ! Avec une quantité très importante à la fois de malt et de houblon,
on obtient une bière puissante,
épaisse mais également amère et fruitée,
ce qui lui confère un équilibre étonnamment agréable à boire."
                                            },
                                            new Beer
                                            {
                                                Name = "Proper Job",
                                                AlcoholRate = 5.5,
                                                BreweryName = "St Austell",
                                                CountryName = "Angleterre",
                                                Price = 6.5,
Description = @"Bière dorée de fermentation haute,
la Proper Job est une IPA brassée en Cornouaille.
Rafraîchissante et équilibrée,
sa légèreté est relevée d'une belle amertume soulignant de puissants arômes d'agrumes dûs à un houblonnage très travaillé."
                                            }
                                        }
                                    },
                                    { "Blonde Au Miel",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Mélissandre",
                                                AlcoholRate = 6.0,
                                                BreweryName = "Les Faiseurs De Bière",
                                                CountryName = "Suisse",
                                                Price = 6.0,
Description = @"Bière de haute fermentation,
où du miel est ajouté en fin de cuisson. Brassée de manière artisanale,
son goût doux séduira les amateurs de bière sucrée."
                                            }
                                        }
                                    },
                                    { "Ambrée Au Miel",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Bière de Miel",
                                                AlcoholRate = 8.0,
                                                BreweryName = "Dupont",
                                                CountryName = "Belgique",
                                                Price = 5.5,
Description = @"Bière de haute fermentation brassée exclusivement à partir de matières premières certifiées biologiques et sans additif chimique. Son parfum envoûtant et son goût subtil la rendent très agréablement sucrée sans être écœurante."
                                            }
                                        }
                                    },
                                    { "Premium Bitter",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Bombardier",
                                                AlcoholRate = 5.2,
                                                BreweryName = "Wells",
                                                CountryName = "Angleterre",
                                                Price = 6.0,
Description = @"Bière à haute fermentation,
c'est LA bière britannique authentique par excellence. Riche en malt,
elle reste légère et agréable en bouche."
                                            }
                                        }
                                    },
                                    { "Ambrée Au Malt De Whisky",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Adelscott",
                                                AlcoholRate = 6.6,
                                                BreweryName = "Fischer",
                                                CountryName = "France",
                                                Price = 5.0,
Description = @"Bière de haute fermentation brassée à partir de malt à whisky. Bière sucrée et sans amertume. La persistance aromatique fruitée qui reste en bouche lui donne une légèreté et un goût final agréable."
                                            }
                                        }
                                    },
                                    { "Ale Rousse",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Rivale",
                                                AlcoholRate = 4.6,
                                                BreweryName = "Brasserie Trois Dames",
                                                CountryName = "Suisse",
                                                Price = 5.0,
Description = @"Bière de haute fermentation et d’inspiration irlandaise. Faiblement houblonnée et aux notes douces et maltées (pain toasté,
noisette),
il s’agit d’une bière de « session »(on peut facilement en boire plusieurs en suivant)grâce à son caractère léger et peu alcoolisé."
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        { BeerContainer.BigBottle,
                            new BeerMenuPart
                            {
                                BeersOfTheMonth = new Beer[0],
                                Beers = new Dictionary<string, Beer[]>
                                {
                                    { "Blonde",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Lupulus",
                                                AlcoholRate = 8.5,
                                                BreweryName = "Les 3 Fourquets",
                                                CountryName = "Belgique",
                                                Price = 10.0,
Description = @"Bière de haute fermentation,
refermentée en bouteille,
non filtrée et non pasteurisée. Dotée de touches olfactives et gustatives tout à fait uniques et nouvelles.
A l'époque de nos ancêtres les Loups peuplaient l'Ardenne. On dit que ces hordes venaient de Slovénie,
contrée où les meilleurs houblons du monde,
en latin Humulus lupulus,
voient le jour.
Vrai ou faux? Quoi qu'il en soit une bière de grande classe est née. La LUPULUS,
just FABULUS!"
                                            }
                                        }
                                    },
                                    { "Pale Ale",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "Chouffe Dobbelen IPA Tripel",
                                                AlcoholRate = 9.0,
                                                BreweryName = "D´Achouffe",
                                                CountryName = "Belgique",
                                                Price = 13.0,
Description = @"La Bière Houblon chouffe dobbelen ipa triple est une bière de type""Pale Ale"" à fermentation ,
conçue par le brasseur""Achouffe"" en Belgique. Une India Pale Ale saisissant par son extraordinaire amertume qui se prolonge par un corps moelleux,
levuré et alcoolisé. Une IPA à la sauce belge !"
                                            }
                                        }
                                    },
                                    { "Strong Sour Ale",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "L'Abbaye de Saint Bon Chien",
                                                AlcoholRate = 11.0,
                                                BreweryName = "BFM",
                                                CountryName = "Suisse",
                                                Price = 14.0,
Description = @"Cette bière de fermentation spontanée,
est une cuvée aux reflets rouges-ambrés. Elle est mûrie pendant de longs mois dans des fûts de chêne ayant déjà contenu du vin ou des eaux-de-vie. Ces fûts donneront des arômes très complexes à cette bière."
                                            }
                                        }
                                    },
                                    { "Brune",
                                        new[]
                                        {
                                            new Beer
                                            {
                                                Name = "McChouffe",
                                                AlcoholRate = 8.5,
                                                BreweryName = "D´Achouffe",
                                                CountryName = "Belgique",
                                                Price = 13.0,
Description = @"Bière de haute fermentation,
refermentée en bouteille,
non filtrée,
non pasteurisée et sans additifs.
Forte,
fruitée,
épicée,
légèrement houblonnée,
au goût évolutif."
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            );
        }
    }
}
#endif
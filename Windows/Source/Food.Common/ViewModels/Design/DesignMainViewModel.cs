// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Food.Models;

// Design data for MainViewModel

namespace PocketCampus.Food.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public Restaurant[] Menu
        {
            get
            {
                return new[]
                {
                    new Restaurant
                    {
                        Name = "Atlantide",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Suprême de poulet (CH) sauce curry jaune",
                                Description = @"Jardinière de légumes
Riz parfumé
Buffet de salades",
                                MealTypes = new[] { MealType.Thai },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 9.50 },
                                                                             { PriceTarget.Visitor, 12.00 },
                                                                             { PriceTarget.PhDStudent, 9.50 },
                                                                             { PriceTarget.Staff, 9.50 }},
                                HalfPortionPrice = 7.50
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Cafétéria BC",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Ragout de porc (CH) mixed-grill au vinaigre balsamique",
                                Description = @"Épinards en branches
Pomme purée",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                             { PriceTarget.Visitor, 10.50 },
                                                                             { PriceTarget.PhDStudent, 8.00 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "Tagliatelle au pesto et basilic, graines de courge",
                                Description = @"Épinards en branches
Salade mêlée",
                                MealTypes = new[] { MealType.Vegetarian, MealType.Pasta },
                                Prices = new Dictionary<PriceTarget, double>{}
                            },
                            new Meal
                            {
                                Name = "Piccata de dinde (CH), sauce à la tomate",
                                Description = @"Épinards en branches
Pomme purée
Salade mêlée",
                                MealTypes = new[] { MealType.Poultry },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 12.00 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Bistro 31",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Steak de Boeuf",
                                Description = @"Haricots verts
Pommes de terre au four
Salade",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 25.00 }}
                            }
                        }
                    },
                    new Restaurant
                    {
                        Name = "Le Copernic",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Pavé de loup de mer à l'huile vierge",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 26.00 }}
                            },
                            new Meal
                            {
                                Name = "Filet de rouget grondin",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 26.00 }}
                            },
                            new Meal
                            {
                                Name = "Saladine d'oranges sanguine, sébaste aux crevettes",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 20.00 }}
                            },
                            new Meal
                            {
                                Name = "Escalope de saumon poêlé à la piperade",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 18.50 }}
                            },
                            new Meal
                            {
                                Name = "Pavé de boeuf Angus Irlandais, cuisson à votre convenance",
                                Description = "",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 27.00 }}
                            },
                            new Meal
                            {
                                Name = "Panzerrotti au jambon cru, crème de basilic",
                                Description = "",
                                MealTypes = new[] { MealType.Pasta },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 18.50 }}
                            },
                            new Meal
                            {
                                Name = "Filet de volaille FR jaune farcie, sauce aux morilles",
                                Description = "",
                                MealTypes = new[] { MealType.Poultry },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 25.00 }}
                            },
                            new Meal
                            {
                                Name = "Tartare de boulgour ou Gaspacho comme un bloody Mary",
                                Description = "",
                                MealTypes= new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 8.00 }}
                            },
                            new Meal
                            {
                                Name = "Risotto de polenta aux pleurotes",
                                Description = "Bol de salade",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 18.50 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Le Corbusier",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Cuisse de poulet à la diable(ch)",
                                Description = @"Pommes frites
Carottes Vichy
ou salade ou potage",
                                MealTypes = new[] { MealType.Poultry },
                                Prices  =new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                             { PriceTarget.Visitor, 10.50 },
                                                                             { PriceTarget.PhDStudent, 8.00 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "Fricandeaux aux petits oignons",
                                Description = @"Pâtes au beurre
Carottes Vichy
Salade ou potage",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 11.00 },
                                                                             { PriceTarget.Visitor, 11.00 },
                                                                             { PriceTarget.PhDStudent, 11.00 },
                                                                             { PriceTarget.Staff, 11.00 }}
                            },
                            new Meal
                            {
                                Name = "Pojarki de veau grillé",
                                Description = @"Pâtes au beurre
Légume du jour
Salade ou potage",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 9.00 },
                                                                             { PriceTarget.Visitor, 10.00 },
                                                                             { PriceTarget.PhDStudent, 11.00 },
                                                                             { PriceTarget.Staff, 12.00 }}
                            },
                            new Meal
                            {
                                Name = "Pizza ou pâtes fraiches maison",
                                Description = @"sauce crème ou tomate
garniture à choi
Xaussi à l'emporter",
                                MealTypes = new[] { MealType.Pizza },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 9.00 },
                                                                             { PriceTarget.Visitor, 9.00 },
                                                                             { PriceTarget.PhDStudent, 9.00 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "penne à la diavola",
                                Description = @"Légumes,épices,tomate et sauge
Salade ou potage",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                             { PriceTarget.Visitor, 10.50 },
                                                                             { PriceTarget.PhDStudent, 8.00 },
                                                                             { PriceTarget.Staff, 9.50 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "L'Esplanade",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Steak de cheval(CA) Jus a la diable",
                                Description = @"Haricots verts
Pommes de terre au four",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                             { PriceTarget.Visitor, 10.50 },
                                                                             { PriceTarget.PhDStudent, 8.00 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Le Hodler",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Entrecôte de boeuf, double cheese burger",
                                Description = @"Légumes du jour
ou garniture du jour",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 14.00 }}
                            },
                            new Meal
                            {
                                Name = "Hamburger",
                                Description = @"Légumes du jour
ou garniture du jour",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 12.00 }}
                            },
                            new Meal
                            {
                                Name = "Cheese burger",
                                Description = @"Légumes du jour
ou garniture du jour",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 12.50 }}
                            },
                            new Meal
                            {
                                Name = "Marguerite CHF9.00",
                                Description = @"Végétarienne, calzone, 3 fromages
Reine
Pizza de la semaine : roquette, poivrons, crevettes et parmesan",
                                MealTypes = new[] { MealType.Pizza },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 10.00 }}
                            },
                            new Meal
                            {
                                Name = "Poitrine de poulet à l'ail d'ours",
                                Description = @"Carottes persillées
Riz pilaw",
                                MealTypes = new[] { MealType.Poultry },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                             { PriceTarget.Visitor, 10.50 },
                                                                             { PriceTarget.PhDStudent, 8.00 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "Dal de lentilles corail",
                                Description = @"aux épices douces
Salade mêlée",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 10.00 }}
                            }
                        }
                    },
                    new Restaurant
                    {
                        Name = "Hong Thai Rung",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Nouilles jaunes sautées aux crevettes",
                                Description = "",
                                MealTypes = new[] { MealType.Thai },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.00 }}
                            },
                            new Meal
                            {
                                Name = "Boeuf (CH) sauté au basilic thaï",
                                Description = "Riz parfumé",
                                MealTypes = new[] { MealType.Meat, MealType.Thai },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.00 }}
                            },
                            new Meal
                            {
                                Name = "Porc (CH) au curry rouge, lait de coco et ananas",
                                Description = "Riz parfumé",
                                MealTypes = new[] { MealType.Meat, MealType.Thai },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.00 }}
                            },
                            new Meal
                            {
                                Name = "Tofu et légumes sautés au gingembre",
                                Description = "Riz parfumé",
                                MealTypes = new[] { MealType.Vegetarian, MealType.Thai },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.00 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Maharaja",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Petits carrés de porc (CH) frits au piment",
                                Description = @"Dhal (lentilles)
Riz basmati",
                                MealTypes = new[] { MealType.Meat, MealType.Indian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.50 }}
                            },
                            new Meal
                            {
                                Name = "Mélange de légumes et d'épices assorties",
                                Description = @"Raita (yogourt épicé)
Riz basmati",
                                MealTypes = new[] { MealType.Vegetarian, MealType.Indian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.50 }}
                            },
                            new Meal
                            {
                                Name = "Pois chiches à la sauce épicée",
                                Description = @"Raita (yogourt épicé)
Riz basmati",
                                MealTypes = new[] { MealType.Vegetarian, MealType.Indian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.50 }}
                            },
                            new Meal
                            {
                                Name = "Émincé de poulet (CH) sauce Tikka Masala",
                                Description = @"Dhal (lentilles)
Riz basmati",
                                MealTypes = new[] { MealType.Poultry, MealType.Indian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.50 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Cafétéria MX",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Petits filets de poulet (CH) aux champignons chinois",
                                Description = @"Petits pois et carottes
Riz au curcuma
Entrée : Potage",
                                MealTypes = new[] { MealType.Poultry },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                             { PriceTarget.Visitor, 12.00 },
                                                                             { PriceTarget.PhDStudent, 8.00 },
                                                                             { PriceTarget.Staff, 9.50 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Obeirut Lebanese Cuisine",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Arousse Chawarma Lahmé",
                                Description = "Emincé de viande marinée rôtie à la broche, persil, cornichon, oignon, tomate et sauce tarator",
                                MealTypes = new[] { MealType.Meat, MealType.Lebanese },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 7.90 }}
                            },
                            new Meal
                            {
                                Name = "Poulet à l'Oriental",
                                Description = "Poulet au four et sauce brune accompagnériz à la viande hachée, épices d'orient, pignons de pins, amandes et pistaches roties",
                                MealTypes = new[] { MealType.Poultry, MealType.Lebanese },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 11.50 }}
                            },
                            new Meal
                            {
                                Name = "Menu Plateau Arousse Kafta",
                                Description = @"Brochette de viande hachée grillée assaisonnée avec du persil et oignon
+ Hommos, Moutabbal
+ Tabboulé
Entrée : + Pain Libanais",
                                MealTypes = new[] { MealType.Poultry, MealType.Lebanese },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 14.90 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Ornithorynque",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Crevettes sautées (VIE) ail et persil plat",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 10.20 },
                                                                             { PriceTarget.Visitor, 12.00 },
                                                                             { PriceTarget.PhDStudent, 10.20 },
                                                                             { PriceTarget.Staff, 12.00 }}
                            },
                            new Meal
                            {
                                Name = "Risotto au jambon (CH), petits pois et parmesan",
                                Description = "",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 11.05 },
                                                                             { PriceTarget.Visitor, 13.00 },
                                                                             { PriceTarget.PhDStudent, 11.05 },
                                                                             { PriceTarget.Staff, 13.00 }}
                            },
                            new Meal
                            {
                                Name = "Lasagne Verdura",
                                Description = "Salade verte",
                                MealTypes = new[] { MealType.Vegetarian, MealType.Pasta },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.65 },
                                                                             { PriceTarget.Visitor, 9.00 },
                                                                             { PriceTarget.PhDStudent, 7.65 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "Pâtes sauce à l'arrabiata",
                                Description = "Salade verte",
                                MealTypes = new[] { MealType.Vegetarian, MealType.Pasta },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.65 },
                                                                             { PriceTarget.Visitor, 9.00 },
                                                                             { PriceTarget.PhDStudent, 7.65 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "Tartine aux légumes du soleil",
                                Description = @"Salade verte",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.65 },
                                                                             { PriceTarget.Visitor, 9.00 },
                                                                             { PriceTarget.PhDStudent, 7.65 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Le Parmentier",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Petits filets de poulet (CH) aux champignons chinois",
                                Description = @"Petits pois et carottes
Riz au curcuma",
                                MealTypes = new[] { MealType.Poultry },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                             { PriceTarget.Visitor, 10.50 },
                                                                             { PriceTarget.PhDStudent, 8.00 },
                                                                             { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "Saltimbocca de porc (CH) vénitienne",
                                Description = @"Brocolis vapeur
Pommes de terre sautées",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 12.00 },
                                                                             { PriceTarget.Visitor, 12.00 },
                                                                             { PriceTarget.PhDStudent, 12.00 },
                                                                             { PriceTarget.Staff, 12.00 }}
                            },
                            new Meal
                            {
                                Name = "haché végétal au curry jaune et haricots coco",
                                Description = "Salade de céleri betterave au gomasio",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 7.00 },
                                                                           { PriceTarget.Visitor, 10.50 },
                                                                           { PriceTarget.PhDStudent, 8.00 },
                                                                           { PriceTarget.Staff, 9.00 }}
                            },
                            new Meal
                            {
                                Name = "Omelette au gruyère et fines herbes",
                                Description = @"Petits légumes
Petits palets de roestis au four",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.Student, 9.00 },
                                                                             { PriceTarget.Visitor, 12.00 },
                                                                             { PriceTarget.PhDStudent, 10.00 },
                                                                             { PriceTarget.Staff, 11.00 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "Le Puur Innovation",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Salade Niçoise avec thon et olives",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 12.50 }}
                            },
                            new Meal
                            {
                                Name = "Gigot d'agneau (NZ) avec sauce aux échalotes et miel. Riz pilaw",
                                Description = "",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 13.90 }}
                            },
                            new Meal
                            {
                                Name = "Escalope de quasi de porc (CH) avec sauce au citron vert, courgettes et pommes de terre à l'origan",
                                Description = "",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 14.90 }}
                            },
                            new Meal
                            {
                                Name = "Pâtes fraiches maisonà la carbonara avec lardons (CH), jambon (CH), oignons et sauce au parmesan",
                                Description = "",
                                MealTypes = new[] { MealType.Meat, MealType.Pasta },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 13.50 }}
                            },
                            new Meal
                            {
                                Name = "Pâtes fraiches maison à la Carbonara di verdura avec légumes, oignons et sauce au parmesan",
                                Description = "",
                                MealTypes = new[] { MealType.Vegetarian, MealType.Pasta },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 13.50 }}
                            },
                            new Meal
                            {
                                Name = "Salade Caesar avec poulet (CH) pané",
                                Description = "",
                                MealTypes = new[] { MealType.Poultry },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 12.50 }}
                            },
                            new Meal
                            {
                                Name = "nuggets de poulet sauce aigre douce",
                                Description = "",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 5.90 }}
                            },
                            new Meal
                            {
                                Name = "Salade de taboulé avec boulettes de fallafel",
                                Description = "",
                                MealTypes = new[] { MealType.Vegetarian },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 12.50 }}
                            },
                        }
                    },
                    new Restaurant
                    {
                        Name = "La Table de Vallotton",
                        Meals = new[]
                        {
                            new Meal
                            {
                                Name = "Tartare de thon coupé au couteau accompagné des ses toasts",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 31.00 }}
                            },
                            new Meal
                            {
                                Name = "Mi cuit de thon, marinade minute au gingembre et riz à la coriandre",
                                Description = "",
                                MealTypes = new[] { MealType.Fish },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 31.00 }}
                            },
                            new Meal
                            {
                                Name = "Tous les midi un dessert du jour est proposé selon l'inspiration de notre chef pâtissier",
                                Description = "",
                                MealTypes = new[] { MealType.GreenFork },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 0.00 }}
                            },
                            new Meal
                            {
                                Name = "Salade Gourmande (foie gras, gésiers et magret de canard) et pignons torréfiés",
                                Description = "",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 10.00 }}
                            },
                            new Meal
                            {
                                Name = "Tartare de boeuf coupé au couteau (façcon traditionnelle ou italienne) servi avec ses toasts",
                                Description = "",
                                MealTypes = new[] { MealType.Meat },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 31.00 }}
                            },
                            new Meal
                            {
                                Name = "Suprême de pintade fermière au poivre vert",
                                Description = @"Légumes grillés
Pommes à la lyonnaise",
                                MealTypes = new[] { MealType.Poultry },
                                Prices = new Dictionary<PriceTarget, double>{{ PriceTarget.All, 25.00 }}
                            },
                        }
                    }
                };
            }
        }

        public bool AnyMeals { get { return true; } }
        public bool AnyFilterResults { get { return true; } }
        public MealTime MealTime { get { return MealTime.Lunch; } }
        public DateTime MealDate { get { return DateTime.Now; } }

        public IPluginSettings Settings
        {
            get
            {
                return new DesignPluginSettings
                {
                    PriceTarget = PriceTarget.Student,
                    MaximumBudget = 50.0,
                    DisplayedMealTypes = new[] { MealType.Meat, MealType.Pasta, MealType.Fish },
                };
            }
        }

        private sealed class DesignPluginSettings : IPluginSettings
        {
            public PriceTarget PriceTarget { get; set; }
            public double MaximumBudget { get; set; }
            public MealType[] DisplayedMealTypes { get; set; }
            public Dictionary<MealTime, DateTime> LastVotes { get; set; }
        }
#endif
    }
}
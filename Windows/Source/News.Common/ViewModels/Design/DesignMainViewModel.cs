// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.News.Models;

// Design data for MainViewModel

namespace PocketCampus.News.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public Feed[] Feeds
        {
            get
            {
                return new[]
                {
                    new Feed
                    {
                        Name = "Général",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"Les algues : garde-manger des coraux",
                                Date = new DateTime( 2013, 5, 17, 5, 0, 2 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14783/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Du Léman au Baïkal,  une aventure scientifique",
                                Date = new DateTime( 2013, 5, 15, 5, 0, 2 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14773/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"La structure 3D des protéines entre dans une nouvelle dimension",
                                Date = new DateTime( 2013, 5, 13, 5, 0, 2 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14719/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Défi: coupler vitesse et économie d'énergie",
                                Date = new DateTime( 2013, 5, 8, 5, 0, 2 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14694/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Personnaliser YouTube grâce à SublimeVideo",
                                Date = new DateTime( 2013, 5, 6, 5, 0, 2 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14629/324x182.jpg" )
                            }
                        }
                    },
                    new Feed
                    {
                        Name = "ENAC",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"Les algues : garde-manger des coraux",
                                Date = new DateTime( 2013, 5, 17, 12, 8, 43 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14815/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Du Léman au Baïkal,  une aventure scientifique",
                                Date = new DateTime( 2013, 5, 16, 9, 2, 22 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14781/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"En ville, les actifs jonglent avec les moyens de transport",
                                Date = new DateTime( 2013, 5, 1, 8, 24, 51 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14561/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Etude des impacts non-visuels de skylights dans le cadre de l'habitat",
                                Date = new DateTime( 2013, 5, 3, 22, 58, 3 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14627/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Développer l'énergie hydraulique sans épuiser l'environnement",
                                Date = new DateTime( 2013, 4, 29, 10, 6, 54 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14233/324x182.jpg" )
                            }
                        }
                    },
                    new Feed
                    {
                        Name = "Sciences de base",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"A. Quarteroni nommé conférencier émérite ""Feng Kang 2013""",
                                Date = new DateTime( 2013, 5, 14, 12, 8, 33 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14739/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Han Wu reçoit la médaille d'or des NWMA2013",
                                Date = new DateTime( 2013, 5, 8, 19, 41, 43 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14708/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"D. Kressner reçoit un des prix 2013 pour le meilleur article SIAM",
                                Date = new DateTime( 2013, 4, 24, 15, 12, 51 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14156/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Rencontre avec le nouveau directeur du CECAM",
                                Date = new DateTime( 2013, 4, 16, 5, 0, 2 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13912/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"ALMA : Observer la naissance de l'univers",
                                Date = new DateTime( 2013, 4, 12, 5, 0, 3 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13914/324x182.jpg" )
                            }
                        }
                    },
                    new Feed
                    {
                        Name = "Informatique et Communications",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"Le ""Summer Research Institute SuRI"" 2013",
                                Date = new DateTime( 2013, 5, 15, 9, 48, 48 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14775/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"""L'informatique change la façon dont nous percevons le monde.""",
                                Date = new DateTime( 2013, 5, 9, 12, 24, 54 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14710/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Prix PERL pour 3 start-ups liées à IC",
                                Date = new DateTime( 2013, 5, 3, 15, 30, 10 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14617/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Les technologies de l'information et du calcul au cœur du cerveau",
                                Date = new DateTime( 2013, 4, 26, 9, 40, 59 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14191/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Stocker et interpréter le génome de manière sécurisée",
                                Date = new DateTime( 2013, 4, 19, 10, 23, 19 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14092/324x182.jpg" )
                            }
                        }
                    },
                    new Feed
                    {
                        Name = "Collège des Humanités",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"Une vitrine technologique redonne vie aux manuscrits",
                                Date = new DateTime( 2013, 4, 29, 9, 35, 7 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14227/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"CERN, Venise, Human Brain Project: l'ère du Very Big Data a commencé",
                                Date = new DateTime( 2013, 3, 25, 14, 19, 27 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13755/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Fenêtre ouverte sur la Fondation de l'Hermitage avec François Bugnion",
                                Date = new DateTime( 2013, 3, 21, 15, 54, 41 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13724/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"""Venice Time Machine"": la Cité des Doges modélisée",
                                Date = new DateTime( 2013, 2, 25, 9, 32, 13 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13238/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Découvrez les multiples facettes du Grand Conseil vaudois !",
                                Date = new DateTime( 2012, 10, 17, 16, 39, 4 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/11071/324x182.jpg" )
                            }
                        }
                    },
                    new Feed
                    {
                        Name = "Sciences et Techniques de l'Ingénieur",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"Best Paper Award pour deux membres du LSI",
                                Date = new DateTime( 2012, 7, 11, 15, 31, 38 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/9381/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Des antennes pour sonder la glace en Antarctique et sur mars",
                                Date = new DateTime( 2011, 5, 6, 6, 58, 23 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/2550/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Van de Ville et les mécanismes de la conscience",
                                Date = new DateTime( 2010, 10, 29, 11, 39, 19 ),
                            },
                            new FeedItem
                            {
                                Title = @"Floreano NCCR",
                                Date = new DateTime( 2010, 10, 29, 11, 39, 0 ),
                            }
                        }
                    },
                    new Feed
                    {
                        Name = "Sciences de la Vie",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"Les sciences biomédicales EPFL au 1er rang du classement de Leiden",
                                Date = new DateTime( 2013, 5, 7, 9, 24, 20 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14638/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Nouvelle méthode de comptage de facteurs de transcription par SRM-MS",
                                Date = new DateTime( 2013, 4, 15, 7, 22, 49 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13944/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Prix Leenaards 2013 au Prof. Michele De Palma",
                                Date = new DateTime( 2013, 4, 8, 13, 9, 5 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13881/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"SwissTB Award 2013: le colauréat est Ruben C. Hartkoorn, du GHI",
                                Date = new DateTime( 2013, 3, 22, 20, 50, 58 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13745/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Découverte d'une étape clé de la fabrication des globules rouges",
                                Date = new DateTime( 2013, 3, 18, 9, 23, 33 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/13602/324x182.jpg" )
                            }
                        }
                    },
                    new Feed
                    {
                        Name = "Management de la Technologie",
                        Items = new[]
                        {
                            new FeedItem
                            {
                                Title = @"Nouveau membre de la Comm. d'Experts pour la Recherche & Innovation",
                                Date = new DateTime( 2013, 5, 13, 15, 27, 34 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14741/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Débattre sur des sujets de pertinence globale au G20 Youth Forum",
                                Date = new DateTime( 2013, 5, 6, 14, 47, 6 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14656/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Silicon Valley Startup Camp: partez étudiants, revenez entrepreneur!",
                                Date = new DateTime( 2013, 5, 6, 9, 38, 38 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14636/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Nouvelle publication par Prof. Finger et Ralf Dyllick-Brenzinger",
                                Date = new DateTime( 2013, 4, 30, 10, 12, 13 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14515/324x182.jpg" )
                            },
                            new FeedItem
                            {
                                Title = @"Prof. Chris Tucci élu au Board of Governors à l'Academy of Management",
                                Date = new DateTime( 2013, 4, 19, 8, 54, 48 ),
                                Image = new OnlineImage( "http://actu.epfl.ch/image/14070/324x182.jpg" )
                            }
                        }
                    }
                };
            }
        }
#endif
    }
}
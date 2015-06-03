// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation of INewsService

#if DEBUG
using System;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.News.Models;

namespace PocketCampus.News.Services.Design
{
    public sealed class DesignNewsService : INewsService
    {
        public Task<FeedsResponse> GetFeedsAsync( FeedsRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new FeedsResponse
                {
                    Status = ResponseStatus.Success,
                    Feeds = new[] 
                    { 
                        new Feed
                        {
                            Name = "Général",
                            Items = new[]
                            {
                                new FeedItem
                                {
                                    Title = @"L'EPFL au plus profond de la «jungle aux robots» de Londres",
                                    Date = new DateTime( 2013, 11, 29, 0, 0, 0 ),
                                    ImageUrl = "http://actu.epfl.ch/image/19310/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Les algues : garde-manger des coraux",
                                    Date = new DateTime( 2013, 5, 17, 5, 0, 2 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14783/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Du Léman au Baïkal,  une aventure scientifique",
                                    Date = new DateTime( 2013, 5, 15, 5, 0, 2 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14773/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"La structure 3D des protéines entre dans une nouvelle dimension",
                                    Date = new DateTime( 2013, 5, 13, 5, 0, 2 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14719/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Défi: coupler vitesse et économie d'énergie",
                                    Date = new DateTime( 2013, 5, 8, 5, 0, 2 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14694/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Personnaliser YouTube grâce à SublimeVideo",
                                    Date = new DateTime( 2013, 5, 6, 5, 0, 2 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14629/{x}x{y}.jpg"
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
                                    ImageUrl = "http://actu.epfl.ch/image/14815/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Du Léman au Baïkal,  une aventure scientifique",
                                    Date = new DateTime( 2013, 5, 16, 9, 2, 22 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14781/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"En ville, les actifs jonglent avec les moyens de transport",
                                    Date = new DateTime( 2013, 5, 1, 8, 24, 51 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14561/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Etude des impacts non-visuels de skylights dans le cadre de l'habitat",
                                    Date = new DateTime( 2013, 5, 3, 22, 58, 3 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14627/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Développer l'énergie hydraulique sans épuiser l'environnement",
                                    Date = new DateTime( 2013, 4, 29, 10, 6, 54 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14233/{x}x{y}.jpg"
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
                                    ImageUrl = "http://actu.epfl.ch/image/14739/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Han Wu reçoit la médaille d'or des NWMA2013",
                                    Date = new DateTime( 2013, 5, 8, 19, 41, 43 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14708/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"D. Kressner reçoit un des prix 2013 pour le meilleur article SIAM",
                                    Date = new DateTime( 2013, 4, 24, 15, 12, 51 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14156/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Rencontre avec le nouveau directeur du CECAM",
                                    Date = new DateTime( 2013, 4, 16, 5, 0, 2 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13912/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"ALMA : Observer la naissance de l'univers",
                                    Date = new DateTime( 2013, 4, 12, 5, 0, 3 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13914/{x}x{y}.jpg"
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
                                    ImageUrl = "http://actu.epfl.ch/image/14775/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"""L'informatique change la façon dont nous percevons le monde.""",
                                    Date = new DateTime( 2013, 5, 9, 12, 24, 54 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14710/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Prix PERL pour 3 start-ups liées à IC",
                                    Date = new DateTime( 2013, 5, 3, 15, 30, 10 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14617/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Les technologies de l'information et du calcul au cœur du cerveau",
                                    Date = new DateTime( 2013, 4, 26, 9, 40, 59 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14191/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Stocker et interpréter le génome de manière sécurisée",
                                    Date = new DateTime( 2013, 4, 19, 10, 23, 19 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14092/{x}x{y}.jpg"
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
                                    ImageUrl = "http://actu.epfl.ch/image/14227/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"CERN, Venise, Human Brain Project: l'ère du Very Big Data a commencé",
                                    Date = new DateTime( 2013, 3, 25, 14, 19, 27 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13755/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Fenêtre ouverte sur la Fondation de l'Hermitage avec François Bugnion",
                                    Date = new DateTime( 2013, 3, 21, 15, 54, 41 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13724/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"""Venice Time Machine"": la Cité des Doges modélisée",
                                    Date = new DateTime( 2013, 2, 25, 9, 32, 13 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13238/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Découvrez les multiples facettes du Grand Conseil vaudois !",
                                    Date = new DateTime( 2012, 10, 17, 16, 39, 4 ),
                                    ImageUrl = "http://actu.epfl.ch/image/11071/{x}x{y}.jpg"
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
                                    ImageUrl = "http://actu.epfl.ch/image/9381/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Des antennes pour sonder la glace en Antarctique et sur mars",
                                    Date = new DateTime( 2011, 5, 6, 6, 58, 23 ),
                                    ImageUrl = "http://actu.epfl.ch/image/2550/{x}x{y}.jpg"
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
                                    ImageUrl = "http://actu.epfl.ch/image/14638/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Nouvelle méthode de comptage de facteurs de transcription par SRM-MS",
                                    Date = new DateTime( 2013, 4, 15, 7, 22, 49 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13944/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Prix Leenaards 2013 au Prof. Michele De Palma",
                                    Date = new DateTime( 2013, 4, 8, 13, 9, 5 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13881/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"SwissTB Award 2013: le colauréat est Ruben C. Hartkoorn, du GHI",
                                    Date = new DateTime( 2013, 3, 22, 20, 50, 58 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13745/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Découverte d'une étape clé de la fabrication des globules rouges",
                                    Date = new DateTime( 2013, 3, 18, 9, 23, 33 ),
                                    ImageUrl = "http://actu.epfl.ch/image/13602/{x}x{y}.jpg"
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
                                    ImageUrl = "http://actu.epfl.ch/image/14741/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Débattre sur des sujets de pertinence globale au G20 Youth Forum",
                                    Date = new DateTime( 2013, 5, 6, 14, 47, 6 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14656/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Silicon Valley Startup Camp: partez étudiants, revenez entrepreneur!",
                                    Date = new DateTime( 2013, 5, 6, 9, 38, 38 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14636/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Nouvelle publication par Prof. Finger et Ralf Dyllick-Brenzinger",
                                    Date = new DateTime( 2013, 4, 30, 10, 12, 13 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14515/{x}x{y}.jpg"
                                },
                                new FeedItem
                                {
                                    Title = @"Prof. Chris Tucci élu au Board of Governors à l'Academy of Management",
                                    Date = new DateTime( 2013, 4, 19, 8, 54, 48 ),
                                    ImageUrl = "http://actu.epfl.ch/image/14070/{x}x{y}.jpg"
                                }
                            }
                        }
                    }
                }
            );
        }

        public Task<FeedItemContentResponse> GetFeedItemContentAsync( FeedItemContentRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new FeedItemContentResponse
                {
                    Status = ResponseStatus.Success,
                    Content = new FeedItemContent
                    {
                        FeedName = "Général",
                        Title = "L'EPFL au plus profond de la «jungle aux robots» de Londres",
                        Url = "http://actu.epfl.ch/news/l-epfl-au-plus-profond-de-la-jungle-aux-robots-de-",
                        ImageUrl = "http://actu.epfl.ch/image/19310/{x}x{y}.jpg",
                        Content = @"<div>
29.11.13 - Le robot-chat et les salamandres robotiques créées à Lausanne s'aventurent ce week-end dans la jungle robotique du Musée de la science de Londres. Ses visiteurs sont invités à prendre part à un «safari robotique», à la rencontre des plus étonnants spécimens sortis des laboratoires de toute l’Europe.<br />
<p>C'est à un voyage bien particulier que les visiteurs du <em>London Science Museum </em>sont invités ce week-end. L'exposition Robot Safari EU rassemble treize robots bio-inspirés, issus de laboratoires de recherche du Royaume-Uni et d'Europe continentale. ""Il ne s'agit pas seulement de voir comment la nature inspire la robotique, mais aussi comment ces robots biomimétiques nous permettent de mieux comprendre le monde animal et végétal qu'ils imitent"", explique Nicola Burghall, l'une des responsables de l'exposition.</p> <p>L'EPFL contribue largement au dépaysement puisqu'elle s'y présente avec plusieurs spécimens. Les chercheurs lausannois du laboratoire Biorob se servent notamment de ces modèles pour étudier le système nerveux, en reproduisant son fonctionnement pour faire marcher ou nager une salamandre robotique. A terme, ces travaux pourraient permettre le développement de thérapies pour des blessures de la moelle épinière ou de prothèses plus performantes. Les robots de l'EPFL participent au Robot Safari EU grâce au soutien de NCCR Robotics.</p> <p>Petite visite en images (photos: © EPFL / Hillary Sanctuary)...</p> <p><img width=""400"" height=""601"" alt="""" src=""http://actu.epfl.ch/public/upload/fckeditorimage/5c/9b/80ac3c58.jpg"" /><br /> <em>Au-delà de ce portique, vous pénétrez dans la jungle des robots bio-inspirés...</em></p> <p><img width=""600"" height=""399"" alt="""" src=""http://actu.epfl.ch/public/upload/fckeditorimage/a5/41/f06bd331.jpg"" /><br /> <em>Les roboticiens de l'EPFL</em> <em>Massimo Vespignani (g.) et Peter Eckert expliquent aux visiteurs le fonctionnement de Cheetah-cub, le robot-chat.</em></p> <p><br /> <img width=""600"" height=""399"" alt="""" src=""http://actu.epfl.ch/public/upload/fckeditorimage/81/79/973fc1f4.jpg"" /><br /> <em>Derniers ajustements avant la mise à l'eau de Salamandra Robotica II.</em></p> <p><img width=""600"" height=""399"" alt="""" src=""http://actu.epfl.ch/public/upload/fckeditorimage/28/a7/2accdeb9.jpg"" /><br /> <em>Alessandro Crespi prépare la salamandre robotique pour le bain.</em></p> <p><img width=""600"" height=""399"" alt="""" src=""http://actu.epfl.ch/public/upload/fckeditorimage/a6/6b/abaa0e32.jpg"" /><br /> <em>Les ambassadeurs de l'EPFL entourent Pleurobot, Cheetah-cub et Salamandra robotica II. De g. à dr.: Auke Ijspeert, Massimo Vespignani, Robin Thandiackal, Peter Eckert, Kostas Karakasiliotis et Alessandro Crespi.</em></p>
</div>"
                    }
                }
            );
        }
    }
}
#endif
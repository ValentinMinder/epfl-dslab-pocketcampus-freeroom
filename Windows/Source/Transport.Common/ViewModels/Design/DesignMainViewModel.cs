// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using PocketCampus.Common;
using PocketCampus.Transport.Models;
using ThinMvvm;

// Design data for MainViewModel

namespace PocketCampus.Transport.ViewModels.Design
{
    public class DesignMainViewModel
    {
#if DEBUG
        public DataStatus DataStatus { get { return DataStatus.DataLoaded; } }

        public IPluginSettings Settings
        {
            get
            {
                return new DesignPluginSettings( new[]
                {
                    new Station { Name = "EPFL" },
                    new Station { Name = "UNIL-Sorge" },
                    new Station { Name = "Lausanne-Flon" },
                    new Station { Name = "Zurich" },
                    new Station { Name = "Grandvaux" }
                } );
            }
        }

        public GeoLocationStatus LocationStatus
        {
            get { return GeoLocationStatus.Success; }
        }

        public Station SelectedStation
        {
            get { return Settings.Stations[0]; }
        }

        public StationTrips[] Trips
        {
            get
            {
                return new[]
                {
                    new StationTrips( new Station { Name = "Grandvaux" }, new[]
                    {
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 2, 0 ),
                            Arrival = new Station { Name = "Grandvaux" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 38, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 2, 0 ),
                                    Arrival = new Station { Name = "Renens VD, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 9, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Renens VD" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 21, 0 ),
                                    DeparturePosition = "4",
                                    Arrival = new Station { Name = "Grandvaux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 38, 0 ),
                                    ArrivalPosition = "2",
                                    Line = new Line { Name = "SS" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 7, 0 ),
                            Arrival = new Station { Name = "Grandvaux" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 38, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 7, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 20, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 25, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 26, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 30, 0 ),
                                    DeparturePosition = "1",
                                    Arrival = new Station { Name = "Grandvaux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 38, 0 ),
                                    ArrivalPosition = "2",
                                    Line = new Line { Name = "SS" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 14, 0 ),
                            Arrival = new Station { Name = "Grandvaux" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 15, 19, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 14, 0 ),
                                    Arrival = new Station { Name = "Renens VD, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 21, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Renens VD" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 34, 0 ),
                                    DeparturePosition = "5",
                                    Arrival = new Station { Name = "Palézieux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 57, 0 ),
                                    ArrivalPosition = "2",
                                    Line = new Line { Name = "RE" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Palézieux" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 8, 0 ),
                                    DeparturePosition = "3",
                                    Arrival = new Station { Name = "Grandvaux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 19, 0 ),
                                    ArrivalPosition = "3",
                                    Line = new Line { Name = "SS" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                            Arrival = new Station { Name = "Grandvaux" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 15, 19, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 32, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 37, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 38, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 42, 0 ),
                                    DeparturePosition = "1",
                                    Arrival = new Station { Name = "Palézieux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 57, 0 ),
                                    ArrivalPosition = "2",
                                    Line = new Line { Name = "RE" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Palézieux" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 8, 0 ),
                                    DeparturePosition = "3",
                                    Arrival = new Station { Name = "Grandvaux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 19, 0 ),
                                    ArrivalPosition = "3",
                                    Line = new Line { Name = "SS" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 15, 2, 0 ),
                            Arrival = new Station { Name = "Grandvaux" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 15, 38, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 2, 0 ),
                                    Arrival = new Station { Name = "Renens VD, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 9, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Renens VD" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 21, 0 ),
                                    DeparturePosition = "4",
                                    Arrival = new Station { Name = "Grandvaux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 38, 0 ),
                                    ArrivalPosition = "2",
                                    Line = new Line { Name = "SS" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 15, 7, 0 ),
                            Arrival = new Station { Name = "Grandvaux" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 15, 38, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 7, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 20, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 25, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 26, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 30, 0 ),
                                    DeparturePosition = "1",
                                    Arrival = new Station { Name = "Grandvaux" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 38, 0 ),
                                    ArrivalPosition = "2",
                                    Line = new Line { Name = "SS" }
                                }
                            }
                        }
                    } ),
                    new StationTrips( new Station { Name = "Lausanne-Flon" }, new[]
                    {
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 13, 43, 0 ),
                            Arrival = new Station { Name = "Lausanne-Flon" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 13, 56, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 13, 43, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 13, 56, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 13, 55, 0 ),
                            Arrival = new Station { Name = "Lausanne-Flon" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 8, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 13, 55, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 8, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 7, 0 ),
                            Arrival = new Station { Name = "Lausanne-Flon" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 20, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 7, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 20, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                            Arrival = new Station { Name = "Lausanne-Flon" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 32, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 32, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 31, 0 ),
                            Arrival = new Station { Name = "Lausanne-Flon" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 44, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 31, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 44, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 43, 0 ),
                            Arrival = new Station { Name = "Lausanne-Flon" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 56, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 43, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 56, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        }
                    } ),
                    new StationTrips( new Station { Name = "UNIL-Sorge" }, new[]
                    {
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 13, 43, 0 ),
                            Arrival = new Station { Name = "UNIL-Sorge" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 13, 44, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 13, 43, 0 ),
                                    Arrival = new Station { Name = "UNIL-Sorge" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 13, 44, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 13, 55, 0 ),
                            Arrival = new Station { Name = "UNIL-Sorge" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 13, 56, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 13, 55, 0 ),
                                    Arrival = new Station { Name = "UNIL-Sorge" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 13, 56, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 7, 0 ),
                            Arrival = new Station { Name = "UNIL-Sorge" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 8, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 7, 0 ),
                                    Arrival = new Station { Name = "UNIL-Sorge" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 8, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                            Arrival = new Station { Name = "UNIL-Sorge" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 20, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                                    Arrival = new Station { Name = "UNIL-Sorge" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 20, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 31, 0 ),
                            Arrival = new Station { Name = "UNIL-Sorge" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 32, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 31, 0 ),
                                    Arrival = new Station { Name = "UNIL-Sorge" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 32, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 43, 0 ),
                            Arrival = new Station { Name = "UNIL-Sorge" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 14, 44, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 43, 0 ),
                                    Arrival = new Station { Name = "UNIL-Sorge" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 44, 0 ),
                                    Line = new Line { Name = "M1" }
                                }
                            }
                        }
                    } ),
                    new StationTrips( new Station { Name = "Zurich" }, new[]
                    {
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 13, 55, 0 ),
                            Arrival = new Station { Name = "Zürich HB" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 16, 28, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 13, 55, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 8, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 13, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 14, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 20, 0 ),
                                    DeparturePosition = "1",
                                    Arrival = new Station { Name = "Zürich HB" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 16, 28, 0 ),
                                    ArrivalPosition = "9",
                                    Line = new Line { Name = "IC" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                            Arrival = new Station { Name = "Zürich HB" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 16, 56, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 19, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 32, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 37, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 14, 38, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 45, 0 ),
                                    DeparturePosition = "8",
                                    Arrival = new Station { Name = "Zürich HB" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 16, 56, 0 ),
                                    ArrivalPosition = "10",
                                    Line = new Line { Name = "ICN" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 14, 55, 0 ),
                            Arrival = new Station { Name = "Zürich HB" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 17, 28, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 14, 55, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 8, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 12, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 13, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 20, 0 ),
                                    DeparturePosition = "1",
                                    Arrival = new Station { Name = "Zürich HB" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 17, 28, 0 ),
                                    ArrivalPosition = "8",
                                    Line = new Line { Name = "IC" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 15, 19, 0 ),
                            Arrival = new Station { Name = "Zürich HB" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 17, 56, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 19, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 32, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 37, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 15, 38, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 45, 0 ),
                                    DeparturePosition = "8",
                                    Arrival = new Station { Name = "Biel/Bienne" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 16, 43, 0 ),
                                    ArrivalPosition = "3",
                                    Line = new Line { Name = "ICN" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Biel/Bienne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 16, 46, 0 ),
                                    DeparturePosition = "2",
                                    Arrival = new Station { Name = "Zürich HB" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 17, 56, 0 ),
                                    ArrivalPosition = "10",
                                    Line = new Line { Name = "ICN" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 15, 55, 0 ),
                            Arrival = new Station { Name = "Zürich HB" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 18, 28, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 15, 55, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 16, 8, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 16, 13, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 16, 14, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 16, 20, 0 ),
                                    DeparturePosition = "1",
                                    Arrival = new Station { Name = "Zürich HB" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 18, 28, 0 ),
                                    ArrivalPosition = "9",
                                    Line = new Line { Name = "IC" }
                                }
                            }
                        },
                        new Trip
                        {
                            Departure = new Station { Name = "EPFL" },
                            DepartureTime = new DateTime( 2013, 12, 14, 16, 19, 0 ),
                            Arrival = new Station { Name = "Zürich HB" },
                            ArrivalTime = new DateTime( 2013, 12, 14, 18, 56, 0 ),
                            Connections = new[]
                            {
                                new Connection
                                {
                                    Departure = new Station { Name = "EPFL" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 16, 19, 0 ),
                                    Arrival = new Station { Name = "Lausanne-Flon" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 16, 32, 0 ),
                                    Line = new Line { Name = "M1" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne-Flon" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 16, 37, 0 ),
                                    Arrival = new Station { Name = "Lausanne, gare" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 16, 38, 0 ),
                                    Line = new Line { Name = "M2" }
                                },
                                new Connection
                                {
                                    Departure = new Station { Name = "Lausanne" },
                                    DepartureTime = new DateTime( 2013, 12, 14, 16, 45, 0 ),
                                    DeparturePosition = "8",
                                    Arrival = new Station { Name = "Zürich HB" },
                                    ArrivalTime = new DateTime( 2013, 12, 14, 18, 56, 0 ),
                                    ArrivalPosition = "8",
                                    Line = new Line { Name = "ICN" }
                                }
                            }
                        }
                    } )
                };
            }
        }

        private sealed class DesignPluginSettings : IPluginSettings
        {
            public bool SortByPosition { get { return false; } set { } }

            public ObservableCollection<Station> Stations { get; set; }

            public DesignPluginSettings( IEnumerable<Station> stations )
            {
                Stations = new ObservableCollection<Station>( stations );
            }
        }
#endif
    }
}
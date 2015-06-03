// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ITransportService

#if DEBUG
using System;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Transport.Models;

namespace PocketCampus.Transport.Services.Design
{
    public sealed class DesignTransportService : ITransportService
    {
        public Task<StationSearchResponse> SearchStationsAsync( StationSearchRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new StationSearchResponse
                {
                    Status = TransportStatus.Success,
                    Stations = new[] 
                    {
                        new Station
                        {
                            Name = "UNIL-Dorigny"
                        },
                        new Station
                        {
                            Name = "UNIL-Mouline"
                        },
                        new Station
                        {
                            Name = "UNIL-Sorge"
                        }
                    }
                }
            );
        }

        public Task<DefaultStationsResponse> GetDefaultStationsAsync( CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new DefaultStationsResponse
                {
                    Status = TransportStatus.Success,
                    Stations = new[] 
                    {
                        new Station
                        {
                            Name = "EPFL"
                        },
                        new Station
                        {
                            Name = "UNIL-Sorge"
                        },
                        new Station
                        {
                            Name = "Lausanne-Flon"
                        }
                    }
                }
            );
        }

        public Task<TripSearchResponse> SearchTripsAsync( TripSearchRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new TripSearchResponse
                {
                    Status = TransportStatus.Success,
                    // from will be EPFL since it's the first above
                    Trips = request.To.Name == "UNIL-Sorge" ?
                    new[] 
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
                    }
                        // if not UNIL-Sorge, then Flon
                  : new[]
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
                    }
                }
            );
        }
    }
}
#endif
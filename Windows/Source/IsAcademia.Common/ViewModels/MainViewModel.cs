// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.IsAcademia.Models;
using PocketCampus.IsAcademia.Services;
using PocketCampus.Map;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.IsAcademia.ViewModels
{
    /// <summary>
    /// The main (and only) ViewModel.
    /// </summary>
    [LogId( "/isacademia" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private const int MinimumDaysInWeek = 5;

        private readonly IIsAcademiaService _isaService;
        private readonly ISecureRequestHandler _requestHandler;

        private StudyDay[] _days;
        private DateTime _weekDate;

        /// <summary>
        /// Gets the available days.
        /// </summary>
        public StudyDay[] Days
        {
            get { return _days; }
            private set { SetProperty( ref _days, value ); }
        }

        /// <summary>
        /// Gets or sets the current week, as a DateTime.
        /// </summary>
        public DateTime WeekDate
        {
            get { return _weekDate; }
            set { SetProperty( ref _weekDate, value ); OnWeekDateChanged(); }
        }

        /// <summary>
        /// Gets the command executed to view a room.
        /// </summary>
        [LogId( "ViewRoomOnMap" )]
        public Command<string> ViewRoomOnMapCommand
        {
            get { return GetCommand<string>( room => Messenger.Send( new MapSearchRequest( room ) ) ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( IIsAcademiaService isaService, ISecureRequestHandler requestHandler )
        {
            _isaService = isaService;
            _requestHandler = requestHandler;

            _weekDate = GetWeekStart( DateTime.Now );
        }


        /// <summary>
        /// Occurs when the WeekDate changes.
        /// </summary>
        private async void OnWeekDateChanged()
        {
            await TryRefreshAsync( true );
        }

        /// <summary>
        /// Fetches the periods and transforms them to a binding-friendly representation.
        /// </summary>
        protected override Task RefreshAsync( CancellationToken token, bool force )
        {
            return _requestHandler.ExecuteAsync<MainViewModel>( async () =>
            {
                if ( !force )
                {
                    return true;
                }

                var request = new ScheduleRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    WeekStart = WeekDate
                };
                var response = await _isaService.GetScheduleAsync( request );
                if ( response.Status == ResponseStatus.AuthenticationError )
                {
                    return false;
                }
                if ( response.Status != ResponseStatus.Success )
                {
                    throw new Exception( "An error occurred on the server while fetching the schedule." );
                }

                if ( !token.IsCancellationRequested )
                {
                    // Now for the fun part!
                    // The days group their periods by UTC date
                    // but since we're in local date, some "days" may hold periods outside of their UTC date
                    // so we have to disassemble them and re-assemble new days
                    var days = response.Days
                                       .SelectMany( d => ForceSameStartAndEndDays( d.Periods ) )
                                       .GroupBy( p => p.Start.Date )
                                       .Select( g => new StudyDay { Day = g.Key, Periods = g.ToArray() } )
                                       .ToArray();
                    var missingDays = Enumerable.Range( 0, MinimumDaysInWeek )
                                                .Select( n => WeekDate.AddDays( n ) )
                                                .Where( d => days.All( d2 => d.Date != d2.Day.Date ) )
                                                .Select( d => new StudyDay { Day = d.Date, Periods = new Period[0] } );
                    Days = days.Concat( missingDays )
                               .OrderBy( d => d.Day )
                               .ToArray();
                }

                return true;
            } );
        }

        /// <summary>
        /// Gets the last specified day of week before or at the specified date.
        /// </summary>
        private static DateTime GetWeekStart( DateTime date )
        {
            return date.Date.Subtract( TimeSpan.FromDays( GetDayIndex( date.DayOfWeek ) - GetDayIndex( DayOfWeek.Monday ) ) );
        }

        /// <summary>
        /// Gets the Monday-based day index for the specified day of week.
        /// </summary>
        private static int GetDayIndex( DayOfWeek dow )
        {
            return dow == DayOfWeek.Sunday ? 6 : (int) dow - 1;
        }

        /// <summary>
        /// Ensures that all periods in the specified sequence begin and end on the same day, splitting them in two if needed.
        /// </summary>
        private static IEnumerable<Period> ForceSameStartAndEndDays( IEnumerable<Period> periods )
        {
            foreach ( var period in periods )
            {
                if ( period.Start.Date == period.End.Date )
                {
                    yield return period;
                }
                else
                {
                    var earlyPeriod = period.Clone();
                    var latePeriod = period.Clone();
                    latePeriod.Start = earlyPeriod.End = period.End.Date;
                    yield return earlyPeriod;
                    yield return latePeriod;
                }
            }
        }
    }
}
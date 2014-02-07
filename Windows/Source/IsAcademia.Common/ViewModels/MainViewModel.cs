// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.IsAcademia.Models;
using PocketCampus.IsAcademia.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.IsAcademia.ViewModels
{
    /// <summary>
    /// The main (and only) ViewModel.
    /// </summary>
    [PageLogId( "/schedule" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private readonly IIsAcademiaService _isaService;
        private readonly ISecureRequestHandler _requestHandler;

        private DayInfo[] _days;
        private DayInfo _currentDay;
        private DateTime _weekDate;

        /// <summary>
        /// Gets the available days.
        /// </summary>
        public DayInfo[] Days
        {
            get { return _days; }
            private set { SetProperty( ref _days, value ); }
        }

        /// <summary>
        /// Gets the current day.
        /// </summary>
        public DayInfo CurrentDay
        {
            get { return _currentDay; }
            private set { SetProperty( ref _currentDay, value ); }
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
        protected override Task RefreshAsync( CancellationToken cancellationToken, bool force )
        {
            return _requestHandler.ExecuteAsync<MainViewModel, AuthenticationToken, string>( _isaService, async token =>
            {
                if ( !force )
                {
                    return true;
                }

                var request = new ScheduleRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    Token = token,
                    WeekStart = WeekDate
                };
                var response = await _isaService.GetScheduleAsync( request );
                if ( response.Status == ResponseStatus.AuthenticationError )
                {
                    return false;
                }
                if ( response.Status != ResponseStatus.Ok )
                {
                    throw new Exception( "An error occurred on the server while fetching the schedule." );
                }

                if ( !cancellationToken.IsCancellationRequested )
                {
                    Days = response.Days.Select( d => new DayInfo( d ) ).ToArray();
                    CurrentDay = Days.FirstOrDefault( d => d.Date == DateTime.Now.Date );
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
    }
}
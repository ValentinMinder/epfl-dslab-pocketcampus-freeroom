// Copyright (c) PocketCampus.Org 2014-15
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
    [LogId( "/isacademia" )]
    public sealed class MainViewModel : CachedDataViewModel<NoParameter, ScheduleResponse>
    {
        private const int DaysInWeek = 7;
        private const int MinimumDaysInWeek = 5;

        private readonly IIsAcademiaService _isaService;
        private readonly ISecureRequestHandler _requestHandler;

        private StudyDay[] _days;
        private DateTime _weekDate;


        public StudyDay[] Days
        {
            get { return _days; }
            private set { SetProperty( ref _days, value ); }
        }

        public DateTime WeekDate
        {
            get { return _weekDate; }
            set { SetProperty( ref _weekDate, value ); OnWeekDateChanged(); }
        }


        [LogId( "ViewRoomOnMap" )]
        [LogParameter( "$Param" )]
        public Command<string> ViewRoomOnMapCommand
        {
            get { return this.GetCommand<string>( room => Messenger.Send( new MapSearchRequest( room ) ) ); }
        }


        public MainViewModel( IDataCache cache, IIsAcademiaService isaService, ISecureRequestHandler requestHandler )
            : base( cache )
        {
            _isaService = isaService;
            _requestHandler = requestHandler;

            _weekDate = GetWeekStart( DateTime.Now );
        }


        private async void OnWeekDateChanged()
        {
            await TryRefreshAsync( true );
        }

        protected override CachedTask<ScheduleResponse> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<ScheduleResponse>();
            }

            Func<Task<ScheduleResponse>> getter = () => _requestHandler.ExecuteAsync( () =>
            {
                var request = new ScheduleRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    WeekStart = WeekDate
                };
                return _isaService.GetScheduleAsync( request, token );
            } );

            if ( DateTime.Now >= WeekDate && ( DateTime.Now - WeekDate ).TotalDays < DaysInWeek )
            {
                // cache ID must be non-zero so that other requests don't think this can be used as a placeholder
                return CachedTask.Create( getter, 1, WeekDate.AddDays( DaysInWeek ) );
            }
            return CachedTask.DoNotCache( getter );
        }

        protected override bool HandleData( ScheduleResponse data, CancellationToken token )
        {
            if ( data == null )
            {
                return false;
            }
            if ( data.Status == ResponseStatus.AuthenticationError )
            {
                _requestHandler.Authenticate<MainViewModel>();
                return false;
            }
            if ( data.Status != ResponseStatus.Success )
            {
                throw new Exception( "An error occurred on the server while fetching the schedule." );
            }

            if ( !token.IsCancellationRequested )
            {
                // Now for the fun part!
                // The days group their periods by UTC date
                // but since we're in local date, some "days" may hold periods outside of their UTC date
                // so we have to disassemble them and re-assemble new days
                var days = data.Days.SelectMany( d => ForceSameStartAndEndDays( d.Periods ) )
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
        }


        private static DateTime GetWeekStart( DateTime date )
        {
            return date.Date.Subtract( TimeSpan.FromDays( GetDayIndex( date.DayOfWeek ) - GetDayIndex( DayOfWeek.Monday ) ) );
        }

        private static int GetDayIndex( DayOfWeek dow )
        {
            return dow == DayOfWeek.Sunday ? 6 : (int) dow - 1;
        }

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
                    earlyPeriod.End = latePeriod.Start = period.End.Date;
                    yield return earlyPeriod;
                    yield return latePeriod;
                }
            }
        }
    }
}
// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Events.Models;
using Windows.ApplicationModel.Resources;

namespace PocketCampus.Events
{
    public sealed class EventItemToHumanDateConverter : ValueConverter<EventItem, string>
    {
        private const string TimeFormat = "t";
        private static readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Events.WindowsRuntime/EventDate" );

        public bool IsCompact { get; set; }

        public override string Convert( EventItem value )
        {
            if ( value == null )
            {
                return "";
            }

            string dateFormat = IsCompact ? "d" : "D";

            if ( value.StartDate == null )
            {
                return "";
            }

            var startDate = value.StartDate.Value;

            if ( value.IsFullDay == true )
            {
                return startDate.ToString( dateFormat );
            }

            if ( value.EndDate == null || startDate == value.EndDate.Value )
            {
                return string.Format( _resources.GetString( "SingleDateTimeFormat" ), startDate.ToString( dateFormat ), startDate.ToString( TimeFormat ) );
            }

            var endDate = value.EndDate.Value;

            if ( endDate.TimeOfDay == TimeSpan.Zero )
            {
                // need to subtract 1 day for reasons I don't really understand
                return string.Format( _resources.GetString( "DifferentDateTimesFormat" ),
                      startDate.ToString( dateFormat ), startDate.ToString( TimeFormat ),
                      endDate.ToString( dateFormat ), endDate.AddDays( -1 ).ToString( TimeFormat ) );
            }

            if ( startDate.Date == endDate.Date )
            {
                return string.Format( _resources.GetString( "SingleDateDifferentTimesFormat" ),
                                      startDate.ToString( dateFormat ),
                                      startDate.ToString( TimeFormat ), endDate.ToString( TimeFormat ) );
            }

            return string.Format( _resources.GetString( "DifferentDateTimesFormat" ),
                                  startDate.ToString( dateFormat ), startDate.ToString( TimeFormat ),
                                  endDate.ToString( dateFormat ), endDate.ToString( TimeFormat ) );
        }
    }
}
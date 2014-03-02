using PocketCampus.Common;
using PocketCampus.Events.Models;
using PocketCampus.Events.Resources;

namespace PocketCampus.Events
{
    public sealed class EventItemToHumanDateConverter : ValueConverter<EventItem, string>
    {
        public bool IsCompact { get; set; }

        public override string Convert( EventItem value )
        {
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
                return string.Format( PluginResources.SingleDateTimeFormat, startDate.ToString( dateFormat ), startDate.ToShortTimeString() );
            }

            var endDate = value.EndDate.Value;

            if ( startDate.Date == endDate.Date )
            {
                return string.Format( PluginResources.SingleDateDifferentTimeFormat, startDate.ToString( dateFormat ),
                                      startDate.ToShortTimeString(), endDate.ToShortTimeString() );
            }

            return string.Format( PluginResources.DifferentDateFormat, startDate.ToString( dateFormat ), startDate.ToShortTimeString(),
                                  endDate.ToString( dateFormat ), endDate.ToShortTimeString() );
        }
    }
}
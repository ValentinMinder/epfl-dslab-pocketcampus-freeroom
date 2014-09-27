using System;
using System.Collections.Generic;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.IsAcademia.Models;
using Windows.UI;
using Windows.UI.Xaml.Media;

namespace PocketCampus.IsAcademia
{
    public sealed class DateToWeekStringConverter : ValueConverter<DateTime, string>
    {
        private const string DifferentMonthFormat = "{0:M} - {1:M}";

        // There's no standard way to display a localized version of "December 2 - 8", since order can vary (e.g. French is "2 - 8 décembre")
        // Simple workaround: Find the number.
        // The percent is necessary for 'd' to be interpreted as 'day between 1 and 31' instead of 'standard date format'.
        private static readonly string SameMonthFormat = DateTime.Now.ToString( "M" ).Split( ' ' )[0].Any( char.IsDigit ) ?
                                                         "{0:%d} - {1:M}"
                                                       : "{0:M} - {1:%d}";

        public override string Convert( DateTime value )
        {
            var end = value.AddDays( 6 );
            string format = value.Month == end.Month ? SameMonthFormat : DifferentMonthFormat;
            return string.Format( format, value, end );
        }
    }

    public sealed class PeriodTypeToBrushConverter : ValueConverter<PeriodType, Brush>
    {
        private static readonly Dictionary<PeriodType, Brush> Values = new Dictionary<PeriodType, Brush>
        {
            { PeriodType.Lecture, new SolidColorBrush( Color.FromArgb( 0xFF, 0x64, 0xC9, 0xDB ) ) },
            { PeriodType.Exercises, new SolidColorBrush( Color.FromArgb( 0xFF, 0x8D, 0xE5, 0x4E ) ) },
            { PeriodType.Project, new SolidColorBrush( Color.FromArgb( 0xFF, 0x67, 0x70, 0x6F ) ) },
            { PeriodType.Lab, new SolidColorBrush( Color.FromArgb( 0xFF, 0xFF, 0xA1, 0x4F ) ) },
            { PeriodType.OralExam, new SolidColorBrush( Color.FromArgb( 0xFF, 0xAF, 0x3F, 0xFF ) ) },
            { PeriodType.WrittenExam, new SolidColorBrush( Color.FromArgb( 0xFF, 0xC5, 0x7A, 0xFF ) ) }
        };

        public override Brush Convert( PeriodType value )
        {
            return Values[value];
        }
    }
}
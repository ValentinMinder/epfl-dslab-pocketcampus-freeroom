// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Windows.Media;
using PocketCampus.Common;
using PocketCampus.Schedule.Models;

namespace PocketCampus.Schedule
{
    /// <summary>
    /// Converts a period type to a brush.
    /// </summary>
    public sealed class PeriodTypeToBrushConverter : ValueConverter<PeriodType, Brush>
    {
        private static Dictionary<PeriodType, Brush> Values = new Dictionary<PeriodType, Brush>
        {
            { PeriodType.Lecture, new SolidColorBrush( Color.FromArgb( 0xFF, 0x64, 0xC9, 0xDB ) ) },
            { PeriodType.Exercises, new SolidColorBrush( Color.FromArgb( 0xFF, 0x8D, 0xE5, 0x4E ) ) },
            { PeriodType.Project, new SolidColorBrush( Color.FromArgb( 0xFF, 0x67, 0x70, 0x6F ) ) },
            { PeriodType.Lab, new SolidColorBrush( Color.FromArgb( 0xFF, 0xFF, 0xA1, 0x4F ) ) }
        };

        protected override Brush Convert( PeriodType value )
        {
            return Values[value];
        }
    }
}
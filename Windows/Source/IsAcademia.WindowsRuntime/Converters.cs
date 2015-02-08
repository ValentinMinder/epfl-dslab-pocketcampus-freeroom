// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.IsAcademia.Models;
using Windows.UI;
using Windows.UI.Xaml.Media;

namespace PocketCampus.IsAcademia
{
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
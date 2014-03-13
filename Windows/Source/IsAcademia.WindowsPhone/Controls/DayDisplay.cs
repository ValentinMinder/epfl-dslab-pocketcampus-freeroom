// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using PocketCampus.IsAcademia.Models;

//  _    _   ______   _____    ______  
// | |  | | |  ____| |  __ \  |  ____| 
// | |__| | | |__  | | |__) | | |__    
// |  __  | |  __| | |  _  /  |  __|   
// | |  | | | |____| | | \ \  | |____  
// |_|  |_| |______| |_|  \_\ |______| 
//  ____    ______ 
// |  _ \  |  ____|
// | |_) | | |__   
// |  _ <| |  __|  
// | |_) | | |____ 
// |____/  |______|
//  _____    _____               _____    ____    _   _    _____ 
// |  __ \  |  __ \      /\     / ____|  / __ \  | \ | |  / ____|
// | |  | | | |__) |    /  \   | |  __| | |  | | |  \| | | (___  
// | |  | | |  _  /    / /\ \  | | |_ | | |  | | | . ` |  \___ \ 
// | |__| | | | \ \   / ____ \ | |__| | | |__| | | |\  |  ____) |
// |_____/  |_|  \_\ /_/    \_\ \_____|  \____/  |_| \_| /_____/ 
//

// (please don't look at this)
// (I'll refactor this soon, promise)

namespace PocketCampus.IsAcademia.Controls
{
    /// <summary>
    /// Displays one or multiple days.
    /// </summary>
    public sealed class DayDisplay : UserControl
    {
        private const int MinimumHoursInDay = 10;
        private const int HoursInDay = 24;
        private const string HourFormat = @"00\:\0\0";
        private const int MinutesInHour = 60;

        private const int CollisionCheckingInterval = 15; // in minutes
        private const double HoursGridWidth = 50;

        #region Day DependencyProperty
        /// <summary>
        /// The day to display, if any.
        /// </summary>
        public StudyDay Day
        {
            get { return (StudyDay) GetValue( DayProperty ); }
            set { SetValue( DayProperty, value ); }
        }

        public static readonly DependencyProperty DayProperty =
            DependencyProperty.Register( "Day", typeof( StudyDay ), typeof( DayDisplay ),
                new PropertyMetadata( ( o, _ ) => ( (DayDisplay) o ).Refresh() ) );
        #endregion

        #region Days DependencyProperty
        /// <summary>
        /// All of the days in the week.
        /// </summary>
        public StudyDay[] Days
        {
            get { return (StudyDay[]) GetValue( DaysProperty ); }
            set { SetValue( DaysProperty, value ); }
        }

        public static readonly DependencyProperty DaysProperty =
            DependencyProperty.Register( "Days", typeof( StudyDay[] ), typeof( DayDisplay ),
                new PropertyMetadata( ( o, _ ) => ( (DayDisplay) o ).Refresh() ) );
        #endregion

        #region HourIncrement DependencyProperty
        public int HourIncrement
        {
            get { return (int) GetValue( HourIncrementProperty ); }
            set { SetValue( HourIncrementProperty, value ); }
        }

        public static readonly DependencyProperty HourIncrementProperty =
            DependencyProperty.Register( "HourIncrement", typeof( int ), typeof( DayDisplay ),
                new PropertyMetadata( 1, ( o, _ ) => ( (DayDisplay) o ).Refresh() ) );
        #endregion

        #region PeriodTemplate DependencyProperty
        /// <summary>
        /// The template for periods.
        /// </summary>
        public DataTemplate PeriodTemplate
        {
            get { return (DataTemplate) GetValue( PeriodTemplateProperty ); }
            set { SetValue( PeriodTemplateProperty, value ); }
        }

        public static readonly DependencyProperty PeriodTemplateProperty =
            DependencyProperty.Register( "PeriodTemplate", typeof( DataTemplate ), typeof( DayDisplay ), new PropertyMetadata( null ) );
        #endregion

        #region ContainerStyle DependencyProperty
        /// <summary>
        /// The style of the period containers.
        /// </summary>
        public Style ContainerStyle
        {
            get { return (Style) GetValue( ContainerStyleProperty ); }
            set { SetValue( ContainerStyleProperty, value ); }
        }

        public static readonly DependencyProperty ContainerStyleProperty =
            DependencyProperty.Register( "ContainerStyle", typeof( Style ), typeof( DayDisplay ), new PropertyMetadata( null ) );
        #endregion

        #region EmptyContainerStyle DependencyProperty
        /// <summary>
        /// The style of "empty" period containers, i.e. containers for hours without a period.
        /// </summary>
        public Style EmptyContainerStyle
        {
            get { return (Style) GetValue( EmptyContainerStyleProperty ); }
            set { SetValue( EmptyContainerStyleProperty, value ); }
        }

        public static readonly DependencyProperty EmptyContainerStyleProperty =
            DependencyProperty.Register( "EmptyContainerStyle", typeof( Style ), typeof( DayDisplay ), new PropertyMetadata( null ) );
        #endregion

        private int _minHour;
        private int _maxHour;

        public DayDisplay()
        {
            VerticalContentAlignment = VerticalAlignment.Stretch;
            HorizontalContentAlignment = HorizontalAlignment.Stretch;

            Loaded += ( _, __ ) => Refresh();
        }


        /// <summary>
        /// Refresh the displayed periods.
        /// </summary>
        private void Refresh()
        {
            if ( Days == null || ActualWidth == 0 )
            {
                return;
            }

            var minMax = GetMinAndMaxHours( Days );
            _minHour = minMax.Item1;
            _maxHour = minMax.Item2;

            var content = new Grid();
            content.ColumnDefinitions.Add( new ColumnDefinition { Width = new GridLength( HoursGridWidth ) } );
            var hoursGrid = CreateHoursGrid();
            content.Children.Add( hoursGrid );


            if ( Day == null )
            {
                for ( int n = 0; n < Days.Length; n++ )
                {
                    content.ColumnDefinitions.Add( new ColumnDefinition() );
                    var canvas = new Canvas();
                    canvas.Width = ( ActualWidth - HoursGridWidth ) / Days.Length;
                    canvas.Height = ActualHeight;
                    AddPlaceholdersToCanvas( canvas );
                    AddPeriodsToCanvas( canvas, Days[n] );
                    Grid.SetColumn( canvas, n + 1 );
                    content.Children.Add( canvas );
                }
            }
            else
            {
                content.ColumnDefinitions.Add( new ColumnDefinition() );
                var canvas = new Canvas();
                canvas.Width = ActualWidth - HoursGridWidth;
                canvas.Height = ActualHeight;
                AddPlaceholdersToCanvas( canvas );
                AddPeriodsToCanvas( canvas, Day );
                Grid.SetColumn( canvas, 1 );
                content.Children.Add( canvas );
            }

            Content = content;
        }

        /// <summary>
        /// Creates a vertical grid with hours.
        /// </summary>
        private Grid CreateHoursGrid()
        {
            var grid = new Grid();

            for ( int n = _minHour; n < _maxHour; n++ )
            {
                grid.RowDefinitions.Add( new RowDefinition() );
            }

            for ( int hour = _minHour; hour < _maxHour; hour += HourIncrement )
            {
                var block = new TextBlock
                {
                    Text = hour.ToString( HourFormat ),
                    Style = (Style) Application.Current.Resources["PhoneTextSmallStyle"],
                    VerticalAlignment = VerticalAlignment.Top,
                    HorizontalAlignment = HorizontalAlignment.Left,
                    Margin = new Thickness( 1, -5, 1, 0 )
                };
                Grid.SetRow( block, hour - _minHour );
                grid.Children.Add( block );
            }

            return grid;
        }

        private void AddPlaceholdersToCanvas( Canvas canvas )
        {
            int length = _maxHour - _minHour;
            double elementHeight = canvas.Height / length;

            for ( int row = 0; row < length; row++ )
            {
                var border = new Border
                {
                    Height = elementHeight,
                    Width = canvas.Width,
                    Style = EmptyContainerStyle
                };
                Canvas.SetTop( border, elementHeight * row );
                canvas.Children.Add( border );
            }
        }

        private void AddPeriodsToCanvas( Canvas canvas, StudyDay day )
        {
            int columnCount = day.Periods.Select( p => day.Periods.Count( p2 => DoPeriodsIntersect( p, p2 ) ) )
                 .Aggregate( 1, LeastCommonMultiple );

            var dividers = GetWidthDividers( day );
            double heightPerMinute = canvas.Height / ( ( _maxHour - _minHour ) * MinutesInHour );
            double widthPerColumn = canvas.Width / columnCount;
            var startDate = day.Day.AddHours( _minHour );

            int[,] usedSpaces = new int[columnCount, ( _maxHour - _minHour ) * MinutesInHour / CollisionCheckingInterval];


            foreach ( var period in day.Periods )
            {
                int elemWidth = columnCount / dividers[period];

                int startColumn = 0;
                while ( true )
                {
                    int max = 0;
                    for ( int col = 0; col < elemWidth; col++ )
                    {
                        for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                        {
                            max = Math.Max( max, usedSpaces[col + startColumn, (int) ( date - startDate ).TotalMinutes / CollisionCheckingInterval] );
                        }
                    }

                    if ( max == 0 )
                    {
                        // yay!
                        break;
                    }

                    startColumn += elemWidth;
                    if ( startColumn >= columnCount )
                    {
                        System.Diagnostics.Debug.WriteLine( "Error while building periods for day {0}", day.Day.ToShortTimeString() );
                        return; // give up, but don't crash!
                    }
                }

                for ( int col = 0; col < elemWidth; col++ )
                {
                    for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                    {
                        usedSpaces[col + startColumn, (int) ( date - startDate ).TotalMinutes / CollisionCheckingInterval]++;
                    }
                }

                var control = new ContentControl
                {
                    ContentTemplate = PeriodTemplate,
                    Content = period,
                    Style = ContainerStyle,
                    Height = ( period.End - period.Start ).TotalMinutes * heightPerMinute,
                    Width = elemWidth * widthPerColumn
                };

                Canvas.SetTop( control, ( period.Start - startDate ).TotalMinutes * heightPerMinute );
                Canvas.SetLeft( control, widthPerColumn * startColumn );

                canvas.Children.Add( control );
            }
        }

        private Dictionary<Period, int> GetWidthDividers( StudyDay day )
        {
            int[] collisionsPerInterval = new int[HoursInDay * MinutesInHour / CollisionCheckingInterval];

            foreach ( var period in day.Periods )
            {
                for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                {
                    collisionsPerInterval[(int) ( date - day.Day ).TotalMinutes / CollisionCheckingInterval]++;
                }
            }

            var dividers = new Dictionary<Period, int>();
            foreach ( var period in day.Periods )
            {
                int max = 0;
                for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                {
                    max = Math.Max( max, collisionsPerInterval[(int) ( date - day.Day ).TotalMinutes / CollisionCheckingInterval] );
                }
                dividers.Add( period, max );
            }

            return dividers;
        }

        /// <summary>
        /// Finds the Least Common Multiple (LCM) of two integers.
        /// </summary>
        private static int LeastCommonMultiple( int a, int b )
        {
            int num1 = Math.Max( a, b ), num2 = Math.Min( a, b );
            for ( int i = 1; i <= num2; i++ )
            {
                if ( ( num1 * i ) % num2 == 0 )
                {
                    return i * num1;
                }
            }
            return num2;
        }

        private static bool DoPeriodsIntersect( Period p1, Period p2 )
        {
            return ( p1.Start == p2.Start && p1.End == p2.End )
                || ( p1.Start <= p2.Start && p1.End > p2.Start )
                || ( p1.Start >= p2.Start && p1.Start < p2.End );
        }

        private static Tuple<int, int> GetMinAndMaxHours( StudyDay[] days )
        {
            int min = days.Min( d => d.Periods.Min( p => p.Start.Hour ) );
            int max = days.Max( d => d.Periods.Max( p => p.End.Hour ) );
            if ( min + MinimumHoursInDay > HoursInDay )
            {
                min = max - MinimumHoursInDay;
            }
            else
            {
                max = Math.Max( max, min + MinimumHoursInDay );
            }
            return Tuple.Create( min, max );
        }
    }
}
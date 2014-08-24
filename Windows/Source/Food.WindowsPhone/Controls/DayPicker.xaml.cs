// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading;
using System.Windows;
using System.Windows.Markup;
using PocketCampus.Common.Controls;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.Controls
{
    /// <summary>
    /// Picks days of the year.
    /// </summary>
    public partial class DayPicker : ObservableControl
    {
        #region Value DependencyProperty
        /// <summary>
        /// The selected date.
        /// </summary>
        public DateTime Value
        {
            get { return (DateTime) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( DateTime ), typeof( DayPicker ), new PropertyMetadata( DateTime.Now ) );
        #endregion

        #region TextStyle DependencyProperty
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( DayPicker ), new PropertyMetadata( null ) );
        #endregion

        /// <summary>
        /// Gets the command executed to select the previous day.
        /// </summary>
        [LogId( "PreviousDay" )]
        public Command PreviousCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( -1 ) ); }
        }

        /// <summary>
        /// Gets the command executed to select the next day.
        /// </summary>
        [LogId( "NextDay" )]
        public Command NextCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( 1 ) ); }
        }


        /// <summary>
        /// Creates a new DayPicker.
        /// </summary>
        public DayPicker()
        {
            // HACK: Force StringFormat to use the right culture
            Language = XmlLanguage.GetLanguage( Thread.CurrentThread.CurrentUICulture.TwoLetterISOLanguageName );

            InitializeComponent();
            LayoutRoot.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }
    }
}
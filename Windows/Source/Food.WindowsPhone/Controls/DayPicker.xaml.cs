// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading;
using System.Windows;
using System.Windows.Markup;
using PocketCampus.Common.Controls;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

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

        /// <summary>
        /// Gets the command executed to select the previous day.
        /// </summary>
        [CommandLogId( "PreviousDay" )]
        public Command PreviousCommand
        {
            get { return GetCommand( () => Value = Value.AddDays( -1 ) ); }
        }

        /// <summary>
        /// Gets the command executed to select the next day.
        /// </summary>
        [CommandLogId( "NextDay" )]
        public Command NextCommand
        {
            get { return GetCommand( () => Value = Value.AddDays( 1 ) ); }
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
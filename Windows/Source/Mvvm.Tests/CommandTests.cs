// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;
using System.Windows.Input;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace PocketCampus.Mvvm.Tests
{
    [TestClass]
    public sealed class CommandTests
    {
        [TestMethod]
        public void ExecuteCallsTheProvidedExecuteMethod()
        {
            int count = 0;
            bool paramOk = false;
            object obj = new object();
            var cmd = new Command<object>( o =>
            {
                count++;
                if ( o == obj )
                {
                    paramOk = true;
                }
            } );

            cmd.Execute( obj );

            Assert.AreEqual( 1, count, "Execute() should call the provided 'execute' parameter exactly once." );
            Assert.AreEqual( true, paramOk, "Execute() should provide the correct parameter." );
        }

        [TestMethod]
        public void ICommandExecuteCallsTheProvidedExecuteMethod()
        {
            int count = 0;
            bool paramOk = false;
            object obj = new object();
            var cmd = new Command<object>( o =>
            {
                count++;
                if ( o == obj )
                {
                    paramOk = true;
                }
            } );

            ( (ICommand) cmd ).Execute( obj );

            Assert.AreEqual( 1, count, "ICommand.Execute() should call the provided 'execute' parameter exactly once." );
            Assert.AreEqual( true, paramOk, "ICommand.Execute() should provide the correct parameter." );
        }

        [TestMethod]
        public void CanExecuteIsTrueWhenNotProvided()
        {
            var cmd = new Command<object>( _ => { } );

            Assert.AreEqual( true, cmd.CanExecute( null ), "CanExecute() should return true when the 'canExecute' parameter is not provided." );
        }

        [TestMethod]
        public void CanExecuteCallsTheProvidedCanExecuteMethod()
        {
            object obj = new object();
            var cmd = new Command<object>( _ => { }, o => o == obj );

            Assert.AreEqual( false, cmd.CanExecute( new object() ), "CanExecute() should call the provided 'canExecute' parameter with the correct parameter." );
            Assert.AreEqual( true, cmd.CanExecute( obj ), "CanExecute() should call the provided 'canExecute' parameter with the correct parameter." );
        }

        [TestMethod]
        public void ICommandCanExecuteCallsTheProvidedCanExecuteMethod()
        {
            object obj = new object();
            var cmd = new Command<object>( _ => { }, o => o == obj );

            Assert.AreEqual( false, ( (ICommand) cmd ).CanExecute( new object() ), "ICommand.CanExecute() should call the provided 'canExecute' parameter." );
            Assert.AreEqual( true, ( (ICommand) cmd ).CanExecute( obj ), "ICommand.CanExecute() should call the provided 'canExecute' parameter." );
        }

        private sealed class InpcExample : INotifyPropertyChanged
        {
            public int Value { get; set; }

            public void TestAsyncCommand()
            {
                var cmd = new Command<object>( _ => { }, _ => Value == 0 );
                int count = 0;

                cmd.CanExecuteChanged += ( s, e ) => count++;
                OnPropertyChanged( "Value" );

                Assert.AreEqual( 1, count, "CanExecuteChanged should be fired exactly once when a property it uses changes." );
            }

            public event PropertyChangedEventHandler PropertyChanged;
            private void OnPropertyChanged( string propertyName )
            {
                var evt = PropertyChanged;
                if ( evt != null )
                {
                    evt( this, new PropertyChangedEventArgs( propertyName ) );
                }
            }
        }

        [TestMethod]
        public void CanExecuteChangedShouldBeFiredWhenAPropertyChanges()
        {
            new InpcExample().TestAsyncCommand();
        }
    }
}
// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;
using System.Threading.Tasks;
using System.Windows.Input;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace PocketCampus.Mvvm.Tests
{
    [TestClass]
    public sealed class ParameterlessAsyncCommandTests
    {
        [TestMethod]
        public async Task ExecuteAsyncCallsTheProvidedExecuteMethod()
        {
            int count = 0;
            var cmd = new AsyncCommand( null, () => { count++; return Task.FromResult( 0 ); } );

            await cmd.ExecuteAsync();

            Assert.AreEqual( 1, count, "ExecuteAsync() should call the provided 'execute' parameter exactly once." );
        }

        [TestMethod]
        public void ICommandExecuteCallsTheProvidedExecuteMethod()
        {
            int count = 0;
            var cmd = new AsyncCommand( null, () => { count++; return Task.FromResult( 0 ); } );

            ( (ICommand) cmd ).Execute( null );

            Assert.AreEqual( 1, count, "ICommand.Execute() should call the provided 'execute' parameter exactly once." );
        }

        [TestMethod]
        public void CanExecuteIsTrueWhenNotProvided()
        {
            var cmd = new AsyncCommand( null, () => Task.FromResult( 0 ) );

            Assert.AreEqual( true, cmd.CanExecute(), "CanExecute() should return true when the 'canExecute' parameter is not provided." );
        }

        [TestMethod]
        public void CanExecuteCallsTheProvidedCanExecuteMethod()
        {
            int n = 0;
            var cmd = new AsyncCommand( null, () => Task.FromResult( 0 ), () => n == 42 );

            Assert.AreEqual( false, cmd.CanExecute(), "CanExecute() should call the provided 'canExecute' parameter." );
            n = 42;
            Assert.AreEqual( true, cmd.CanExecute(), "CanExecute() should call the provided 'canExecute' parameter." );
        }

        [TestMethod]
        public void ICommandCanExecuteCallsTheProvidedCanExecuteMethod()
        {
            int n = 0;
            var cmd = new AsyncCommand( null, () => Task.FromResult( 0 ), () => n == 42 );

            Assert.AreEqual( false, ( (ICommand) cmd ).CanExecute( null ), "ICommand.CanExecute() should call the provided 'canExecute' parameter." );
            n = 42;
            Assert.AreEqual( true, ( (ICommand) cmd ).CanExecute( null ), "ICommand.CanExecute() should call the provided 'canExecute' parameter." );
        }

        private sealed class InpcExample : INotifyPropertyChanged
        {
            public int Value { get; set; }

            public void TestAsyncCommand()
            {
                var cmd = new AsyncCommand( null, () => Task.FromResult( 0 ), () => Value == 0 );
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
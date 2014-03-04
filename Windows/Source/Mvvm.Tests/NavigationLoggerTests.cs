// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Mvvm.Tests
{
    public class TestNavigationLogger : NavigationLogger
    {
        public List<string> ViewModelNavigations { get; private set; }

        public List<Tuple<string, string, string>> CommandNavigations { get; private set; }

        public TestNavigationLogger()
        {
            ViewModelNavigations = new List<string>();
            CommandNavigations = new List<Tuple<string, string, string>>();
        }

        protected override void LogNavigation( string id )
        {
            ViewModelNavigations.Add( id );
        }

        protected override void LogEvent( string viewModelId, string eventId, string label )
        {
            CommandNavigations.Add( Tuple.Create( viewModelId, eventId, label ) );
        }
    }

    [LogId( "1" )]
    public class TestViewModel1 : ViewModel<NoParameter>
    {
        [LogId( "C1" )]
        public Command Command1
        {
            get { return GetCommand( () => { } ); }
        }

        [LogId( "C2" )]
        public Command Command2
        {
            get { return GetCommand( () => { } ); }
        }
    }

    [LogId( "2" )]
    public class TestViewModel2 : ViewModel<NoParameter>
    {
        public Tuple<string> SomeValue { get; set; }

        [LogId( "C3" )]
        public Command Command3
        {
            get { return GetCommand( () => { } ); }
        }

        [LogId( "C4" )]
        [LogParameter( "SomeValue.Item1" )]
        public Command Command4
        {
            get { return GetCommand( () => { } ); }
        }

        [LogId( "C5" )]
        [LogParameter( "$Param" )]
        public Command<string> Command5
        {
            get { return GetCommand<string>( _ => { } ); }
        }

        [LogId( "C6" )]
        [LogParameter( "$Param" )]
        [LogValueConverter( true, "Yes" )]
        [LogValueConverter( false, "No" )]
        public Command<bool> Command6
        {
            get { return GetCommand<bool>( _ => { } ); }
        }
    }

    [TestClass]
    public class NavigationLoggerTests
    {
        [TestMethod]
        public void ViewModelNavigationIsLogged()
        {
            var logger = new TestNavigationLogger();

            logger.LogNavigation( new TestViewModel1(), true );

            CollectionAssert.AreEqual( new[] { "1" }, logger.ViewModelNavigations );
        }

        [TestMethod]
        public void ViewModelNavigationsAreLogged()
        {
            var logger = new TestNavigationLogger();

            logger.LogNavigation( new TestViewModel1(), true );
            logger.LogNavigation( new TestViewModel2(), true );

            CollectionAssert.AreEqual( new[] { "1", "2" }, logger.ViewModelNavigations );
        }

        [TestMethod]
        public void BackwardsViewModelNavigationIsNotLogged()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel1();

            logger.LogNavigation( vm, true );
            logger.LogNavigation( new TestViewModel2(), true );
            logger.LogNavigation( vm, false );

            CollectionAssert.AreEqual( new[] { "1", "2" }, logger.ViewModelNavigations );
        }

        [TestMethod]
        public void CommandNavigationIsLogged()
        {
            var logger = new TestNavigationLogger();

            var vm = new TestViewModel1();
            logger.LogNavigation( vm, true );

            vm.Command1.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "C1", "" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void CommandNavigationsAreLogged()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel1();

            logger.LogNavigation( vm, true );

            vm.Command1.Execute();
            vm.Command2.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "C1", "" ), Tuple.Create( "1", "C2", "" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void CommandNavigationIsLoggedAfterViewModelChange()
        {
            var logger = new TestNavigationLogger();
            var vm1 = new TestViewModel1();
            var vm2 = new TestViewModel2();

            logger.LogNavigation( vm1, true );
            logger.LogNavigation( vm2, true );

            vm2.Command3.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "2", "C3", "" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void CommandNavigationIsLoggedAfterBackwardsViewModelChange()
        {
            var logger = new TestNavigationLogger();
            var vm1 = new TestViewModel1();
            var vm2 = new TestViewModel2();

            logger.LogNavigation( vm1, true );
            logger.LogNavigation( vm2, true );
            logger.LogNavigation( vm1, false );

            vm1.Command1.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "C1", "" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void CommandLoggingRequestIsHonored()
        {
            var logger = new TestNavigationLogger();
            var vm1 = new TestViewModel1();
            var vm2 = new TestViewModel2();

            logger.LogNavigation( vm1, true );

            Messenger.Send( new CommandLoggingRequest( vm2 ) );

            vm2.Command3.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "C3", "" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void EventLogRequestIsHonored()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel1();

            logger.LogNavigation( vm, true );

            Messenger.Send( new EventLogRequest( "XYZ", "123" ) );

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "XYZ", "123" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void LogParametersRelativeToViewModelAreHonored()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel2 { SomeValue = Tuple.Create( "a b c" ) };

            logger.LogNavigation( vm, true );

            vm.Command4.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "2", "C4", "a b c" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void LogParametersRelativeToCommandParameterAreHonored()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel2();

            logger.LogNavigation( vm, true );

            vm.Command5.Execute( "x y z" );

            CollectionAssert.AreEqual( new[] { Tuple.Create( "2", "C5", "x y z" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void LogValueConvertersAreHonored()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel2();

            logger.LogNavigation( vm, true );

            vm.Command6.Execute( true );
            vm.Command6.Execute( false );

            CollectionAssert.AreEqual( new[] { Tuple.Create( "2", "C6", "Yes" ), Tuple.Create( "2", "C6", "No" ) }, logger.CommandNavigations );
        }
    }
}
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

        public List<Tuple<string, string>> CommandNavigations { get; private set; }

        public TestNavigationLogger()
        {
            ViewModelNavigations = new List<string>();
            CommandNavigations = new List<Tuple<string, string>>();
        }

        protected override void LogNavigation( string id )
        {
            ViewModelNavigations.Add( id );
        }

        protected override void LogEvent( string viewModelId, string eventId )
        {
            CommandNavigations.Add( Tuple.Create( viewModelId, eventId ) );
        }
    }

    [PageLogId( "1" )]
    public class TestViewModel1 : ViewModel<NoParameter>
    {
        [CommandLogId( "C1" )]
        public Command Command1
        {
            get { return GetCommand( () => { } ); }
        }

        [CommandLogId( "C2" )]
        public Command Command2
        {
            get { return GetCommand( () => { } ); }
        }
    }

    [PageLogId( "2" )]
    public class TestViewModel2 : ViewModel<NoParameter>
    {
        [CommandLogId( "C3" )]
        public Command Command3
        {
            get { return GetCommand( () => { } ); }
        }
    }

    [TestClass]
    public class NavigationLoggerTests
    {
        [TestMethod]
        public void ViewModelNavigationIsLogged()
        {
            var logger = new TestNavigationLogger();

            logger.LogNavigation( new TestViewModel1() );

            CollectionAssert.AreEqual( new[] { "1" }, logger.ViewModelNavigations );
        }

        [TestMethod]
        public void ViewModelNavigationsAreLogged()
        {
            var logger = new TestNavigationLogger();

            logger.LogNavigation( new TestViewModel1() );
            logger.LogNavigation( new TestViewModel2() );

            CollectionAssert.AreEqual( new[] { "1", "2" }, logger.ViewModelNavigations );
        }

        [TestMethod]
        public void CommandNavigationIsLogged()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel1();

            logger.LogNavigation( vm );

            vm.Command1.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "C1" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void CommandNavigationsAreLogged()
        {
            var logger = new TestNavigationLogger();
            var vm = new TestViewModel1();

            logger.LogNavigation( vm );

            vm.Command1.Execute();
            vm.Command2.Execute();
            vm.Command1.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "C1" ), Tuple.Create( "1", "C2" ), Tuple.Create( "1", "C1" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void CommandNavigationIsLoggedAfterViewModelChange()
        {
            var logger = new TestNavigationLogger();
            var vm1 = new TestViewModel1();
            var vm2 = new TestViewModel2();

            logger.LogNavigation( vm1 );
            logger.LogNavigation( vm2 );

            vm2.Command3.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "2", "C3" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void CommandLoggingRequestIsHonored()
        {
            var logger = new TestNavigationLogger();
            var vm2 = new TestViewModel2();

            logger.LogNavigation( new TestViewModel1() );

            Messenger.Send( new CommandLoggingRequest( vm2 ) );

            vm2.Command3.Execute();

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "C3" ) }, logger.CommandNavigations );
        }

        [TestMethod]
        public void EventLogRequestIsHonored()
        {
            var logger = new TestNavigationLogger();
            logger.LogNavigation( new TestViewModel1() );

            Messenger.Send( new EventLogRequest( "XYZ" ) );

            CollectionAssert.AreEqual( new[] { Tuple.Create( "1", "XYZ" ) }, logger.CommandNavigations );
        }
    }
}
// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace PocketCampus.Mvvm.Tests
{
    [TestClass]
    public class MessengerTests
    {
        private sealed class IntWrapper
        {
            public int Value { get; set; }

            public void Increment( int incr )
            {
                Value += incr;
            }
        }

        public static void ForceGC()
        {
            // this should do the trick
            GC.Collect( int.MaxValue, GCCollectionMode.Forced, true );
        }

        public static int Counter { get; set; }

        [TestCleanup]
        public void Cleanup()
        {
            Counter = 0;
            Messenger.Clear();
        }

        [TestMethod]
        public void SendDoesNothingWhenNothingIsRegistered()
        {
            Messenger.Send( 42 );
            // no check - the call must simply not throw an exception
        }

        [TestMethod]
        public void SendWorksWithRegisteredLambdas()
        {
            Messenger.Register<int>( n => Counter += n );

            ForceGC();

            Messenger.Send( 42 );

            Assert.AreEqual( 42, Counter, "Send() should work correctly with Register()-ed lambdas." );
        }

        [TestMethod]
        public void SendWorksWithRegisteredClosures()
        {
            int incr = 42;
            Messenger.Register<int>( _ => Counter += incr );

            ForceGC();

            Messenger.Send( 0 );

            Assert.AreEqual( 42, Counter, "Send() should work correctly with Register()-ed closures." );
        }

        [TestMethod]
        public void SendWorksWithRegisteredMethods()
        {
            var wrapper = new IntWrapper { Value = 0 };
            Messenger.Register<int>( wrapper.Increment );

            ForceGC();

            Messenger.Send( 42 );

            Assert.AreEqual( 42, wrapper.Value, "Send() should work correctly with Register()-ed methods." );
        }

        [TestMethod]
        public void SendWorksWithMultipleRecipients()
        {
            Messenger.Register<int>( n => Counter += n );
            Messenger.Register<int>( n => Counter -= 2 * n );

            ForceGC();

            Messenger.Send( 42 );

            Assert.AreEqual( -42, Counter, "Send() should work with multiple Register()-ed recipients." );
        }

        [TestMethod]
        public void SendWorksWithDifferentRecipientTypes()
        {
            Messenger.Register<string>( s => Counter += 2 * int.Parse( s ) );
            Messenger.Register<int>( n => Counter += n );

            ForceGC();

            Messenger.Send( "21" );

            Assert.AreEqual( 42, Counter, "Send() should work with Register()-ed recipients even if other recipient types have been Register()-ed." );
        }

        [TestMethod]
        public void RegisterDoesNotKeepStrongReferences()
        {
            var wrapper = new IntWrapper();
            // N.B. using WeakReference is a terrible idea because of race conditions, but it's useful here
            var wrapperRef = new WeakReference( wrapper );
            Messenger.Register<int>( wrapper.Increment );
            wrapper = null;

            ForceGC();

            Assert.IsFalse( wrapperRef.IsAlive, "Register() should not keep strong references." );
        }
    }
}
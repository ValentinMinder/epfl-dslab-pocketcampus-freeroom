// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace PocketCampus.Mvvm.Tests
{
    [TestClass]
    public sealed class DataViewModelTests : DataViewModel<NoParameter>
    {
        private int _counter = 0;
        private int _forcedCounter = 0;

        [TestCleanup]
        public void Cleanup()
        {
            _counter = 0;
            _forcedCounter = 0;
            IsLoading = false;
            HasError = false;
            HasNetworkError = false;
            DataViewModelOptions.NetworkExceptionType = typeof( WebException );
        }

        protected override Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( force )
            {
                _forcedCounter++;
            }
            _counter++;

            return Task.FromResult( 0 );
        }

        [TestMethod]
        public void OnNavigatedToForcesRefreshTheFirstTime()
        {
            OnNavigatedTo();
            Assert.AreEqual( _forcedCounter, 1, "OnNavigatedTo() should force a refresh the first time it's called." );
            Assert.AreEqual( _counter, 1, "OnNavigatedTo() should ask for a refresh." );
        }

        [TestMethod]
        public void OnNavigatedToDoesNotForcesRefreshSubsequentTimes()
        {
            OnNavigatedTo();
            OnNavigatedTo();
            Assert.AreEqual( _forcedCounter, 1, "OnNavigatedTo() should önly force a refresh the first time it's called." );
            Assert.AreEqual( _counter, 2, "OnNavigatedTo() should ask for a refresh every time." );
        }

        [TestMethod]
        public void TryRefreshAsyncCallsRefreshAsync()
        {
            TryRefreshAsync( false );
            Assert.AreEqual( _forcedCounter, 0, "TryRefreshAsync(false) should not force a refresh." );
            Assert.AreEqual( _counter, 1, "TryRefreshAsync() should ask for a refresh." );
        }

        [TestMethod]
        public void TryRefreshAsyncCallsRefreshAsyncAndForcesWhenAsked()
        {
            TryRefreshAsync( true );
            Assert.AreEqual( _forcedCounter, 1, "TryRefreshAsync(true) should force a refresh." );
        }

        [TestMethod]
        public void TryExecuteAsyncSetsIsLoading()
        {
            var source = new TaskCompletionSource<int>();
            var task = TryExecuteAsync( _ => source.Task );

            Assert.IsTrue( IsLoading, "TryExecuteAsync() should set IsLoading to true while the function is executing." );

            source.SetResult( 0 );

            Assert.IsFalse( IsLoading, "TryExecuteAsync() should set IsLoading to false once the function has completed." );
        }

        [TestMethod]
        public void TryExecuteAsyncDoesNotSetHasErrorsWhenNoErrorOccurs()
        {
            var _ = TryExecuteAsync( __ => Task.FromResult( 0 ) );

            Assert.IsFalse( HasError, "TryExecuteAsync() should not set HasError to true when no error occurs." );
            Assert.IsFalse( HasNetworkError, "TryExecuteAsync() should not set HasNetworkError to true when no error occurs." );
        }

        [TestMethod]
        public void TryExecuteAsyncSetsHasErrorInCaseOfError()
        {
            var source = new TaskCompletionSource<int>();
            var task = TryExecuteAsync( __ => source.Task );

            source.SetException( new Exception() );

            Assert.IsTrue( HasError, "TryExecuteAsync() should set HasError to true when an error occurs." );
            Assert.IsFalse( HasNetworkError, "TryExecuteAsync() should not set HasNetworkError to true when no network error occurs." );
        }

        [TestMethod]
        public void TryExecuteAsyncSetsHasNetworkErrorInCaseOfNetworkError()
        {
            var source = new TaskCompletionSource<int>();
            var task = TryExecuteAsync( __ => source.Task );

            source.SetException( new WebException() );

            Assert.IsFalse( HasError, "TryExecuteAsync() should not set HasError to true when a network error occurs." );
            Assert.IsTrue( HasNetworkError, "TryExecuteAsync() should set HasNetworkError to true when a network error occurs." );
        }

        [TestMethod]
        public void DataViewModelOptionsNetworkExceptionTypeIsRespected()
        {
            DataViewModelOptions.NetworkExceptionType = typeof( DuplicateWaitObjectException );

            var source = new TaskCompletionSource<int>();
            var task = TryExecuteAsync( _ => source.Task );

            source.SetException( new DuplicateWaitObjectException() );

            Assert.IsFalse( HasError, "TryExecuteAsync() should not set HasError to true when a network error occurs." );
            Assert.IsTrue( HasNetworkError, "TryExecuteAsync() should set HasNetworkError to true when a network error occurs." );
        }

        [TestMethod]
        public void TrySetAsyncDoesNotTurnOffIsLoadingWhenCancelling()
        {
            var source = new TaskCompletionSource<int>();

            var task1 = TryExecuteAsync( async tok =>
            {
                while ( true )
                {
                    await Task.Delay( 2 );
                    tok.ThrowIfCancellationRequested();
                }
            } );

            var task2 = TryExecuteAsync( _ => source.Task );

            Assert.IsTrue( IsLoading, "TryExecuteAsync() should not set IsLoading to false when the task has been cancelled." );

            source.SetResult( 0 );
        }

        [TestMethod]
        public void TrySetAsyncDoesNotTurnOnHasErrorWhenCancelling()
        {
            var source = new TaskCompletionSource<int>();

            var task1 = TryExecuteAsync( async tok =>
            {
                while ( true )
                {
                    await Task.Delay( 2 );
                    tok.ThrowIfCancellationRequested();
                }
            } );

            var task2 = TryExecuteAsync( _ => source.Task );

            Assert.IsFalse( HasError, "TryExecuteAsync() should not set HasError to true when the task has been cancelled." );
            Assert.IsFalse( HasNetworkError, "TryExecuteAsync() should not set HasNetworkError to true when the task has been cancelled." );

            source.SetResult( 0 );
        }
    }
}
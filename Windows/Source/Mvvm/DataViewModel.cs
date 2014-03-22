// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Net;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// ViewModel that loads data.
    /// </summary>
    public abstract class DataViewModel<TArg> : ViewModel<TArg>, IDisposable
    {
        // Lock to ensure cancellation doesn't cause race conditions
        private object _lock = new object();
        // Cancellation source to avoid the problem of older tasks finishing after younger ones, and replacing data
        private CancellationTokenSource _cancellationSource;

        private bool _firstRun;
        private bool _isLoading;
        private bool _hasError;
        private bool _hasNetworkError;

        /// <summary>
        /// Gets the currently used cancellation token.
        /// </summary>
        protected CancellationToken CurrentCancellationToken
        {
            get { return _cancellationSource.Token; }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the ViewModel is loading.
        /// </summary>
        public bool IsLoading
        {
            get { return _isLoading; }
            protected set { SetProperty( ref _isLoading, value ); }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the ViewModel has an error.
        /// </summary>
        public bool HasError
        {
            get { return _hasError; }
            protected set { SetProperty( ref _hasError, value ); }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the ViewModel encountered a network error.
        /// </summary>
        public bool HasNetworkError
        {
            get { return _hasNetworkError; }
            protected set { SetProperty( ref _hasNetworkError, value ); }
        }

        /// <summary>
        /// Command executed to update all data.
        /// </summary>
        [LogId( "Refresh" )]
        public AsyncCommand RefreshCommand
        {
            get { return GetAsyncCommand( () => TryRefreshAsync( true ), () => !IsLoading ); }
        }


        /// <summary>
        /// Creates a new DataViewModel.
        /// </summary>
        protected DataViewModel()
        {
            _cancellationSource = new CancellationTokenSource();
            _firstRun = true;
        }


        /// <summary>
        /// Occurs after the user navigated to the ViewModel.
        /// </summary>
        public virtual async Task OnNavigatedToAsync()
        {
            await TryRefreshAsync( _firstRun );
            _firstRun = false;
        }

        /// <summary>
        /// Refreshes the data.
        /// </summary>
        /// <param name="token">The token used to cancel the refresh.</param>
        /// <param name="force">Whether to force the data refresh.</param>
        protected virtual Task RefreshAsync( CancellationToken token, bool force )
        {
            return Task.Delay( 0 );
        }

        /// <summary>
        /// Attempts to refresh the data.
        /// </summary>
        /// <param name="force">Whether to force the data refresh.</param>
        protected Task TryRefreshAsync( bool force )
        {
            return TryExecuteAsync( tok => RefreshAsync( tok, force ) );
        }

        /// <summary>
        /// Attempts to execute the specified asynchronous action.
        /// </summary>
        protected async Task TryExecuteAsync( Func<CancellationToken, Task> action )
        {
            lock ( _lock )
            {
                if ( !_cancellationSource.IsCancellationRequested )
                {
                    _cancellationSource.Cancel();
                }
                _cancellationSource = new CancellationTokenSource();
            }

            HasError = false;
            HasNetworkError = false;
            IsLoading = true;

            var token = _cancellationSource.Token;

            try
            {
                await action( token );
            }
            catch ( Exception e )
            {
                if ( !token.IsCancellationRequested )
                {
                    if ( DataViewModelOptions.NetworkExceptionType.GetTypeInfo().IsAssignableFrom( e.GetType().GetTypeInfo() ) )
                    {
                        HasNetworkError = true;
                    }
                    else
                    {
                        HasError = true;
                    }
                }
            }

            if ( !token.IsCancellationRequested )
            {
                IsLoading = false;
            }
        }

        #region ViewModel overrides
        /// <summary>
        /// Occurs when the user navigates to the ViewModel.
        /// Do not call this method from a derived class.
        /// </summary>
        public async override void OnNavigatedTo()
        {
            await OnNavigatedToAsync();
        }
        #endregion

        #region IDisposable implementation
        /// <summary>
        /// Destroys the DataViewModel.
        /// </summary>
        ~DataViewModel()
        {
            Dispose( false );
        }

        /// <summary>
        /// Disposes of the DataViewModel.
        /// </summary>
        public void Dispose()
        {
            Dispose( true );
            GC.SuppressFinalize( this );
        }

        /// <summary>
        /// Disposes of the DataViewModel (part of the common IDisposable pattern recommended by Microsoft).
        /// </summary>
        protected virtual void Dispose( bool onlyManaged )
        {
            _cancellationSource.Cancel();
            _cancellationSource.Dispose();
        }
        #endregion
    }

    /// <summary>
    /// Options for DataViewModel.
    /// </summary>
    public static class DataViewModelOptions
    {
        private static Type _networkExceptionType = typeof( WebException );

        /// <summary>
        /// Gets or sets the type of exceptions that are considered network exceptions; that is, 
        /// which exceptions will set HasNetworkError to true in DataViewModel.TryExecuteAsync.
        /// WebException by default.
        /// </summary>
        public static Type NetworkExceptionType
        {
            get { return _networkExceptionType; }
            set
            {
                if ( !typeof( Exception ).GetTypeInfo().IsAssignableFrom( value.GetTypeInfo() ) )
                {
                    throw new ArgumentException( "NetworkExceptionType must be an exception type." );
                }
                _networkExceptionType = value;
            }
        }
    }

}
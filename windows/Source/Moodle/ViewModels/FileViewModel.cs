// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.CloudPrint;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Moodle.ViewModels
{
    [LogId( "/moodle/file" )]
    public sealed class FileViewModel : DataViewModel<MoodleFile>
    {
        private static readonly string[] PrintExtensions = { "pdf" };

        private readonly IMoodleService _moodleService;
        private readonly IMoodleDownloader _downloader;
        private readonly IFileStorage _storage;

        private bool _isCached;

        public MoodleFile File { get; private set; }

        public bool IsCached
        {
            get { return _isCached; }
            private set { SetProperty( ref _isCached, value ); }
        }

        [LogId( "ForceDownload" )]
        [LogParameter( "File.Name" )]
        public AsyncCommand ForceDownloadCommand
        {
            get { return this.GetAsyncCommand( ForceDownloadAsync ); }
        }

        [LogId( "Open" )]
        [LogParameter( "File.Name" )]
        public AsyncCommand OpenCommand
        {
            get { return this.GetAsyncCommand( OpenAsync ); }
        }

        [LogId( "Print" )]
        [LogParameter( "File.Name" )]
        public AsyncCommand PrintCommand
        {
            get { return this.GetAsyncCommand( PrintAsync, () => PrintExtensions.Contains( File.Extension ) ); }
        }

        public FileViewModel( IMoodleService moodleService, IMoodleDownloader downloader, IFileStorage storage,
                              MoodleFile file )
        {
            _moodleService = moodleService;
            _downloader = downloader;
            _storage = storage;

            File = file;
        }

        public override async Task OnNavigatedToAsync()
        {
            await base.OnNavigatedToAsync();

            IsCached = await _storage.IsStoredAsync( File );
        }

        private Task ForceDownloadAsync()
        {
            return TryExecuteAsync( async _ =>
            {
                var bytes = await _downloader.DownloadAsync( File );
                await _storage.StoreFileAsync( File, bytes );
                IsCached = true;
            } );
        }

        private async Task OpenAsync()
        {
            if ( !( await _storage.IsStoredAsync( File ) ) )
            {
                await ForceDownloadAsync();
            }
            await _storage.OpenFileAsync( File );
        }

        private Task PrintAsync()
        {
            return TryExecuteAsync( async token =>
            {
                var request = new PrintFileRequest
                {
                    FileUrl = File.DownloadUrl
                };
                var response = await _moodleService.PrintFileAsync( request, token );

                if ( response.Status != MoodleStatus.Success )
                {
                    throw new Exception( "Error while contacting the server." );
                }

                Messenger.Send( new PrintRequest( File.Name, response.DocumentId.Value ) );
            } );
        }
    }
}
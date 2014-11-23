﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Moodle.ViewModels
{
    [LogId( "/moodle/course" )]
    public sealed class CourseViewModel : CachedDataViewModel<Course, CourseSectionsResponse>
    {
        private readonly ISecureRequestHandler _requestHandler;
        private readonly IMoodleService _moodleService;
        private readonly IMoodleDownloader _downloader;
        private readonly IFileStorage _storage;
        private readonly IBrowserService _browserService;

        private CourseSection[] _sections;
        private CourseSection _selectedSection;
        private DownloadStatus _downloadStatus;


        public Course Course { get; set; }

        public CourseSection[] Sections
        {
            get { return _sections; }
            private set { SetProperty( ref _sections, value ); }
        }

        public CourseSection SelectedSection
        {
            get { return _selectedSection; }
            private set { SetProperty( ref _selectedSection, value ); }
        }

        public DownloadStatus DownloadStatus
        {
            get { return _downloadStatus; }
            private set { SetProperty( ref _downloadStatus, value ); }
        }


        [LogId( "DownloadAndOpenFile" )]
        [LogParameter( "$Param.Name" )]
        public AsyncCommand<MoodleFile> OpenFileCommand
        {
            get { return this.GetAsyncCommand<MoodleFile>( OpenFileAsync ); }
        }

        [LogId( "OpenLink" )]
        [LogParameter( "$Param.Name" )]
        public Command<MoodleLink> OpenLinkCommand
        {
            get { return this.GetCommand<MoodleLink>( l => _browserService.NavigateTo( l.Url ) ); }
        }


        public CourseViewModel( IDataCache cache, ISecureRequestHandler requestHandler, IMoodleService moodleService,
                               IMoodleDownloader downloader, IFileStorage storage, IBrowserService browserService,
                               Course course )
            : base( cache )
        {
            _requestHandler = requestHandler;
            _moodleService = moodleService;
            _downloader = downloader;
            _storage = storage;
            _browserService = browserService;
            Course = course;
        }


        protected override CachedTask<CourseSectionsResponse> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<CourseSectionsResponse>();
            }

            return CachedTask.Create( () => _requestHandler.ExecuteAsync( () =>
            {
                var request = new CourseSectionsRequest
                {
                    Language = CultureInfo.CurrentCulture.TwoLetterISOLanguageName,
                    CourseId = Course.Id
                };
                return _moodleService.GetSectionsAsync( request, token );
            } ), Course.Id );
        }

        protected override bool HandleData( CourseSectionsResponse data, CancellationToken token )
        {
            if ( data.Status == MoodleStatus.AuthenticationError )
            {
                _requestHandler.Authenticate<MainViewModel>();
                return false;
            }
            if ( data.Status != MoodleStatus.Success )
            {
                throw new Exception( "An error occurred on the server while fetching sections." );
            }

            if ( !token.IsCancellationRequested )
            {
                // set the files' paths
                foreach ( var section in data.Sections )
                {
                    foreach ( var resource in section.Resources )
                    {
                        if ( resource.File != null )
                        {
                            resource.File.PathComponents = new[] { Course.Name, GetSectionPath( section ) };
                        }
                        if ( resource.Folder != null )
                        {
                            foreach ( var file in resource.Folder.Files )
                            {
                                file.PathComponents = new[] { Course.Name, GetSectionPath( section ), resource.Folder.Name };
                            }
                        }
                    }
                }

                Sections = data.Sections;
                SelectedSection = Sections.FirstOrDefault( s => s.Title == null && s.StartDate.Value <= DateTime.Now && DateTime.Now <= s.EndDate.Value )
                               ?? Sections.FirstOrDefault();
            }

            return true;
        }


        private async Task OpenFileAsync( MoodleFile file )
        {
            if ( DownloadStatus == DownloadStatus.Downloading )
            {
                return;
            }

            try
            {
                if ( !( await _storage.IsStoredAsync( file ) ) )
                {
                    DownloadStatus = DownloadStatus.Downloading;

                    var bytes = await _downloader.DownloadAsync( file );
                    await _storage.StoreFileAsync( file, bytes );
                    DownloadStatus = DownloadStatus.None;

                }
                if ( DownloadStatus == DownloadStatus.None )
                {
                    await _storage.OpenFileAsync( file );
                }
            }
            catch
            {
                DownloadStatus = DownloadStatus.Error;
            }
        }

        private static string GetSectionPath( CourseSection section )
        {
            return section.Title ?? section.StartDate.Value.ToString( "M" );
        }
    }
}
// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
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
        private readonly INavigationService _navigationService;
        private readonly IMoodleService _moodleService;
        private readonly IBrowserService _browserService;

        private CourseSection[] _sections;
        private CourseSection _selectedSection;


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

        [LogId( "OpenLink" )]
        [LogParameter( "$Param.Name" )]
        public Command<MoodleLink> OpenLinkCommand
        {
            get { return this.GetCommand<MoodleLink>( l => _browserService.NavigateTo( l.Url ) ); }
        }

        [LogId( "ViewFile" )]
        [LogParameter( "$Param.Name" )]
        public Command<MoodleFile> ViewFileCommand
        {
            get { return this.GetCommand<MoodleFile>( _navigationService.NavigateTo<FileViewModel, MoodleFile> ); }
        }


        public CourseViewModel( IDataCache cache, ISecureRequestHandler requestHandler, INavigationService navigationService,
                                IMoodleService moodleService, IBrowserService browserService,
                                Course course )
            : base( cache )
        {
            _requestHandler = requestHandler;
            _navigationService = navigationService;
            _moodleService = moodleService;
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

        private static string GetSectionPath( CourseSection section )
        {
            return section.Title ?? section.StartDate.Value.ToString( "M" );
        }
    }
}
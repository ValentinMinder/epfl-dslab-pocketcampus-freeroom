// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using PocketCampus.Authentication;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Moodle.ViewModels
{
    [LogId( "/moodle" )]
    public sealed class MainViewModel : CachedDataViewModel<NoParameter, CoursesResponse>
    {
        private readonly ISecureRequestHandler _requestHandler;
        private readonly INavigationService _navigationService;
        private readonly IMoodleService _moodleService;

        private Course[] _courses;


        public Course[] Courses
        {
            get { return _courses; }
            private set { SetProperty( ref _courses, value ); }
        }

        /// <summary>
        /// Gets the command executed to view a course.
        /// </summary>
        [LogId( "ViewCourse" )]
        [LogParameter( "$Param.LogId" )]
        public Command<Course> ViewCourseCommand
        {
            get { return this.GetCommand<Course>( _navigationService.NavigateTo<CourseViewModel, Course> ); }
        }


        public MainViewModel( IDataCache cache, ISecureRequestHandler requestHandler, INavigationService navigationService,
                              IMoodleService moodleService )
            : base( cache )
        {
            _requestHandler = requestHandler;
            _navigationService = navigationService;
            _moodleService = moodleService;
        }


        protected override CachedTask<CoursesResponse> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<CoursesResponse>();
            }

            return CachedTask.Create( () => _requestHandler.ExecuteAsync( () =>
            {
                var request = new CoursesRequest
                {
                    Language = CultureInfo.CurrentCulture.TwoLetterISOLanguageName
                };
                return _moodleService.GetCoursesAsync( request, token );
            } ) );
        }

        protected override bool HandleData( CoursesResponse data, CancellationToken token )
        {
            if ( data.Status == MoodleStatus.AuthenticationError )
            {
                _navigationService.ForceAuthentication<MainViewModel>();
                return false;
            }
            if ( data.Status != MoodleStatus.Success )
            {
                throw new Exception( "An error occurred on the server while fetching courses." );
            }

            if ( !token.IsCancellationRequested )
            {
                Courses = data.Courses;
            }

            return true;
        }
    }
}
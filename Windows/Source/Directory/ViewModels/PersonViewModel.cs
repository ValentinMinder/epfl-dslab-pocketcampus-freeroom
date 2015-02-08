// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using PocketCampus.Directory.Models;
using PocketCampus.Directory.Services;
using PocketCampus.Map;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Directory.ViewModels
{
    [LogId( "/directory/person" )]
    public sealed class PersonViewModel : ViewModel<Person>
    {
        private readonly IBrowserService _browserService;
        private readonly IEmailService _emailService;
        private readonly IPhoneService _phoneService;
        private readonly IContactsService _contactsService;


        public Person Person { get; private set; }


        [LogId( "ViewOffice" )]
        [LogParameter( "Person.Office" )]
        public Command ViewOfficeCommand
        {
            get { return this.GetCommand( () => Messenger.Send( new MapSearchRequest( Person.Office ) ) ); }
        }

        [LogId( "ViewWebsite" )]
        [LogParameter( "$Param" )]
        public Command<string> OpenWebsiteCommand
        {
            get { return this.GetCommand<string>( _browserService.NavigateTo ); }
        }

        [LogId( "SendEmail" )]
        [LogParameter( "Person.EmailAddress" )]
        public Command SendEmailCommand
        {
            get { return this.GetCommand( () => _emailService.ComposeEmail( Person.EmailAddress ) ); }
        }

        [LogId( "Call" )]
        [LogParameter( "$Param" )]
        public Command<string> CallCommand
        {
            get { return this.GetCommand<string>( num => _phoneService.Call( Person.FullName, num ), _ => _phoneService.CanCall ); }
        }

        [LogId( "CreateNewContact" )]
        [LogParameter( "Person.FullName" )]
        public Command AddAsContactCommand
        {
            get { return this.GetCommand( () => _contactsService.AddAsContact( Person ) ); }
        }


        public PersonViewModel( IBrowserService browserService, IEmailService emailService, IPhoneService phoneService,
                                IContactsService contactsService,
                                Person person )
        {
            _browserService = browserService;
            _emailService = emailService;
            _phoneService = phoneService;
            _contactsService = contactsService;

            Person = person;
        }
    }
}
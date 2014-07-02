// Copyright (c) PocketCampus.Org 2014
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
    /// <summary>
    /// The person ViewModel, used to provide details about one person.
    /// </summary>
    [LogId( "/directory/person" )]
    public sealed class PersonViewModel : ViewModel<Person>
    {
        private readonly IBrowserService _browserService;
        private readonly IEmailService _emailService;
        private readonly IPhoneService _phoneService;
        private readonly IContactsService _contactsService;

        /// <summary>
        /// Gets the person.
        /// </summary>
        public Person Person { get; private set; }

        /// <summary>
        /// Gets the command executed to show the person's office on a map.
        /// </summary>
        [LogId( "ViewOffice" )]
        public Command ViewOfficeCommand
        {
            get { return GetCommand( () => Messenger.Send( new MapSearchRequest( Person.Office ) ) ); }
        }

        /// <summary>
        /// Gets the command executed to open a website.
        /// </summary>
        [LogId( "ViewWebsite" )]
        public Command<string> OpenWebsiteCommand
        {
            get { return GetCommand<string>( _browserService.NavigateTo ); }
        }

        /// <summary>
        /// Gets the command executed to compose an e-mail to the person.
        /// </summary>
        [LogId( "SendEmail" )]
        public Command SendEmailCommand
        {
            get { return GetCommand( () => _emailService.ComposeEmail( Person.EmailAddress ) ); }
        }

        /// <summary>
        /// Gets the command executed to call the person.
        /// </summary>
        [LogId( "Call" )]
        public Command<string> CallCommand
        {
            get { return GetCommand<string>( num => _phoneService.Call( Person.FullName, num ), _ => _phoneService.CanCall ); }
        }

        /// <summary>
        /// Gets the command executed to add the person as a contact.
        /// </summary>
        [LogId( "CreateNewContact" )]
        public Command AddAsContactCommand
        {
            get { return GetCommand( () => _contactsService.AddAsContact( Person ) ); }
        }


        /// <summary>
        /// Creates a new PersonViewModel.
        /// </summary>
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
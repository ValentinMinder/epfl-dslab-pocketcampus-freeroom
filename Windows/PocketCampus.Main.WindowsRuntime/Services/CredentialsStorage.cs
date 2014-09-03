using System.Linq;
using PocketCampus.Common.Services;
using Windows.Security.Credentials;

namespace PocketCampus.Main.Services
{
    public sealed class CredentialsStorage : ICredentialsStorage
    {
        private const string ResourceName = "Tequila";

        private readonly PasswordVault _vault = new PasswordVault();

        /// <summary>
        /// Gets the user name.
        /// </summary>
        public string UserName { get; private set; }

        /// <summary>
        /// Gets the password.
        /// </summary>
        public string Password { get; private set; }


        public CredentialsStorage()
        {
            var cred = _vault.RetrieveAll().FirstOrDefault();
            if ( cred != null )
            {
                cred.RetrievePassword();

                UserName = cred.UserName;
                Password = cred.Password;
            }
        }


        public void SetCredentials( string userName, string password )
        {
            _vault.Add( new PasswordCredential( ResourceName, userName, password ) );
            UserName = userName;
            Password = password;
        }


        public void DeleteCredentials()
        {
            var cred = _vault.RetrieveAll().FirstOrDefault();
            if ( cred != null )
            {
                _vault.Remove( cred );
            }

            UserName = null;
            Password = null;
        }
    }
}
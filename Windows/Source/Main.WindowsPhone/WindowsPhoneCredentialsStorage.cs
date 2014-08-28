using System.Security.Cryptography;
using System.Text;
using PocketCampus.Common.Services;
using ThinMvvm;

namespace PocketCampus.Main
{
    /// <summary>
    /// Windows Phone implementation of ICredentialsStorage.
    /// </summary>
    public sealed class WindowsPhoneCredentialsStorage : SettingsBase<WindowsPhoneCredentialsStorage>, ICredentialsStorage
    {
        // Encoding used when (de)crypting strings as byte arrays
        private static readonly Encoding Encoding = Encoding.UTF8;
        // Salt-like value to add entropy to encrypted strings
        // DO NOT CHANGE or some settings will be unreadable
        private static readonly byte[] EntropyBytes = { 0xDE, 0xAD, 0xBE, 0xEF };


        /// <summary>
        /// Gets or sets the user name.
        /// </summary>
        public string UserName
        {
            get { return Decrypt( Get<byte[]>() ); }
            set { Set( Encrypt( value ) ); }
        }

        /// <summary>
        /// Gets or sets the password.
        /// </summary>
        public string Password
        {
            get { return Decrypt( Get<byte[]>() ); }
            set { Set( Encrypt( value ) ); }
        }


        /// <summary>
        /// Creates a new instance of the WindowsPhoneCredentialsStorage class.
        /// </summary>
        public WindowsPhoneCredentialsStorage( ISettingsStorage settings ) : base( settings ) { }


        /// <summary>
        /// Gets the default values.
        /// </summary>
        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.UserName, () => null },
                { x => x.Password, () => null }
            };
        }


        /// <summary>
        /// Decrypts the specified bytes.
        /// </summary>
        private static string Decrypt( byte[] encrypted )
        {
            if ( encrypted == null )
            {
                return null;
            }

            byte[] decrypted = ProtectedData.Unprotect( encrypted, EntropyBytes );
            return Encoding.GetString( decrypted, 0, decrypted.Length );
        }

        /// <summary>
        /// Encrypts the specified string.
        /// </summary>
        private static byte[] Encrypt( string plain )
        {
            if ( plain == null )
            {
                return null;
            }

            byte[] plainBytes = Encoding.GetBytes( plain );
            return ProtectedData.Protect( plainBytes, EntropyBytes );
        }
    }
}
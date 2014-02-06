// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace PocketCampus.Mvvm.Tests
{
    [TestClass]
    public class ObservableObjectTests : ObservableObject
    {
        private int _value;

        public int Value
        {
            get { return _value; }
            set { SetProperty( ref _value, value ); }
        }

        [TestMethod]
        public void SetPropertySetsTheField()
        {
            _value = 0;

            Value = 42;

            Assert.AreEqual( 42, _value, "SetProperty() should set the field to the correct value." );
        }

        [TestMethod]
        public void SetPropertyDoesNotFirePropertyChangedIfThereWasNoChange()
        {
            Value = 0;
            int counter = 0;
            PropertyChanged += ( s, e ) => counter++;

            Value = 0;

            Assert.AreEqual( 0, counter, "SetProperty() should not fire PropertyChanged when no change occurs." );
        }

        [TestMethod]
        public void SetPropertyFiresPropertyChangedWhenNeeded()
        {
            Value = 0;
            int counter = 0;
            PropertyChanged += ( s, e ) => counter++;

            Value = 42;

            Assert.AreEqual( 1, counter, "SetProperty() should fire PropertyChanged when a change occurs." );
        }
    }
}
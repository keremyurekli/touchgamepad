using Nefarius.ViGEm.Client;
using Nefarius.ViGEm.Client.Targets;
using Nefarius.ViGEm.Client.Targets.DualShock4;
using Nefarius.ViGEm.Client.Targets.Xbox360;
using System.Collections;
using System.Security.Cryptography;
using static System.Runtime.InteropServices.JavaScript.JSType;
using System.Threading;

namespace virtual_gamepad
{
    internal class Program
    {
        


        public static ArrayList buttonGoals = new ArrayList();
        public static ArrayList joystickGoals = new ArrayList();
        public static IXbox360Controller x360;
        static void Main(string[] args)
        {
            Console.WriteLine("Initializing virtual gamepad...");
            var client = new ViGEmClient();
            x360 = client.CreateXbox360Controller();
            Console.WriteLine("Creating "+ x360+ " device...");
            x360.Connect();
            Console.WriteLine("Connected device!");

            while (true)
            {
                string first = Console.ReadLine();

                if (first == "BUTTON")
                {
                    string buttonString = Console.ReadLine();
                    Xbox360Button xboxButton = null;
                    Xbox360Slider xboxSlider = null;
                    switch (buttonString)
                    {
                        case "A":
                            xboxButton = Xbox360Button.A;
                            break;
                        case "B":
                            xboxButton = Xbox360Button.B;
                            break;
                        case "X":
                            xboxButton = Xbox360Button.X;
                            break;
                        case "Y":
                            xboxButton = Xbox360Button.Y;
                            break;
                            //
                        case "R1":
                            xboxSlider = Xbox360Slider.RightTrigger;
                            break;
                        case "L1":
                            xboxSlider = Xbox360Slider.LeftTrigger;
                            break;
                            //
                        case "R2":
                            xboxButton = Xbox360Button.RightShoulder;
                            break;
                        case "L2":
                            xboxButton = Xbox360Button.LeftShoulder;
                            break;
                            //
                        case "R3":
                            xboxButton = Xbox360Button.RightThumb;
                            break;
                        case "L3":
                            xboxButton = Xbox360Button.LeftThumb;
                            break;
                    }
                    int inputValue = Convert.ToInt32(Console.ReadLine());
                    if (xboxButton != null)
                    {
                        ButtonGoal buttonGoal = new ButtonGoal(xboxButton,inputValue);
                        buttonGoals.Add(buttonGoal);

                        //Console.WriteLine(buttonGoal.ToString());
                    } else
                    {
                        SliderGoal sliderGoal = new SliderGoal(xboxSlider,inputValue);
                        buttonGoals.Add(sliderGoal);
                    }
                   

                }
                else if (first == "JOYSTICK")
                {
                    int joystickString = Convert.ToInt32(Console.ReadLine());
                    float inputX = (float)Convert.ToDouble(Console.ReadLine());
                    float inputY = (float)Convert.ToDouble(Console.ReadLine());

                    Xbox360Axis joystickAxisX = null;
                    Xbox360Axis joystickAxisY = null;
                    if (joystickString == 1)
                    {
                        joystickAxisX = Xbox360Axis.LeftThumbX;
                        joystickAxisY = Xbox360Axis.LeftThumbY;
                    }
                    else if (joystickString == 2)
                    {
                        joystickAxisX = Xbox360Axis.RightThumbX;
                        joystickAxisY = Xbox360Axis.RightThumbY;
                    }

                    if (joystickAxisX != null && joystickAxisY != null)
                    {
                        JoystickGoal joystickGoal = new JoystickGoal(joystickAxisX, joystickAxisY, inputX, inputY);
                        joystickGoals.Add(joystickGoal);
                    }

                }else if (first == "TEST")
                {
                    Console.WriteLine("Messaging test is successful, good to go!");
                }

                foreach (var item in buttonGoals)
                {
                    if(item.GetType() == typeof(SliderGoal))
                    {
                        SliderGoal sliderGoal = (SliderGoal)item;
                        x360.SetSliderValue(sliderGoal.slider, (byte)sliderGoal.value);
                    } else
                    {
                        ButtonGoal buttonGoal = (ButtonGoal)item;
                        x360.SetButtonState(buttonGoal.button, buttonGoal.value);
                    }
                   
                }
                buttonGoals.Clear();
                foreach (var item in joystickGoals)
                {
                    JoystickGoal joystickGoal = (JoystickGoal)item;
                    x360.SetAxisValue(joystickGoal.axisX, joystickGoal.x);
                    x360.SetAxisValue(joystickGoal.axisY, joystickGoal.y);
                }
                joystickGoals.Clear();
               
            }

        }
        //static void ButtonCloseCall(ButtonGoal buttonGoal)
        //{
        //    new Thread(() =>
        //    {
        //        Thread.Sleep(200);
        //        if(!buttonGoals.Contains(buttonGoal))
        //        {
        //            x360.SetButtonState(buttonGoal.button, false);
        //        }
        //    }).Start();
        //}
        //static void SliderCloseCall(SliderGoal sliderGoal)
        //{
        //    new Thread(() =>
        //    {
        //        Thread.Sleep(200);
        //        if(!buttonGoals.Contains(sliderGoal))
        //        {
        //            x360.SetSliderValue(sliderGoal.slider, (byte)0);
        //        }
        //    }).Start();
        //}
        static void JoystickResetCall()
        {
            new Thread(() =>
            {
                Thread.Sleep(500);
                if(joystickGoals.Count == 0)
                {
                    x360.SetAxisValue(Xbox360Axis.LeftThumbX, 0);
                    x360.SetAxisValue(Xbox360Axis.LeftThumbY, 0);
                    x360.SetAxisValue(Xbox360Axis.RightThumbX, 0);
                    x360.SetAxisValue(Xbox360Axis.RightThumbY, 0);
                }
              
            }).Start();
        }


        class ButtonGoal
        {
            public Xbox360Button button;

            public bool value;
            public ButtonGoal(Xbox360Button button, int val) {
                this.button = button;
                if(val == 0)
                {
                    value = false;
                }
                else if(val == 1)
                {
                    value = true;
                }
            }
        }

        class SliderGoal
        {
            public Xbox360Slider slider;
            public int value;
            public SliderGoal(Xbox360Slider slider, int value)
            {
                this.slider = slider;
                this.value = value;
            }
        }
        class JoystickGoal
        {
            public Xbox360Axis axisX;
            public Xbox360Axis axisY;
            public short x;
            public short y;
            
            public JoystickGoal(Xbox360Axis axisX, Xbox360Axis axisY, float x, float y)
            {
                this.axisX = axisX;
                this.axisY = axisY;
                this.x = (short)x;
                this.y = (short)y;

            }
        }



    }
}

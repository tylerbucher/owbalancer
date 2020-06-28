import React, {FormEvent} from "react";
import axios from "axios";
// @ts-ignore
import "../node_modules/notific8/src/sass/notific8.scss";
// @ts-ignore
import {Button, Select} from "metro4-react";

type LoginProps = {
    // using `interface` is also ok
};
type LoginState = {
    selectData?: Array<JSX.Element>;
    selectedSelectData?: Array<string>;
    table1Rows?: Array<JSX.Element>;
    table2Rows?: Array<JSX.Element>;
};

class Home extends React.Component<LoginProps, LoginState> {
    constructor(props: LoginProps) {
        super(props);

        this.state = {
            selectData: new Array<JSX.Element>(),
            selectedSelectData: new Array<string>(),
            table1Rows: this.initTable(),
            table2Rows: this.initTable()
        };

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    initTable() {
        let jsxArray = new Array<JSX.Element>();
        for (let i = 0; i < 6; i++) {
            jsxArray.push(this.buildBlankTableRow());
        }
        return jsxArray;
    }

    buildBlankTableRow() {
        return (
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
        );
    }

    buildMultiDataRow(position: String, name: String, sr: String, te: boolean, de: boolean, se: boolean) {
        return (
            <tr>
                <td>{position}</td>
                <td>{name}</td>
                <td>{sr}</td>
                <td>{te}</td>
                <td>{de}</td>
                <td>{se}</td>
            </tr>
        );
    }

    getPositionForId(id: number) {
        switch (id) {
            case 0:
                return "Tank";
            case 1:
                return "Dps";
            case 2:
                return "Support";
        }
    }

    componentDidMount() {
        let sState = this;
        axios.get("/api/v1/users", {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                let mm = new Array<JSX.Element>();
                response.data["api"]["usernameList"].forEach(function (value: Object, index: number) {
                    let key = Object.keys(value)[0];
                    // @ts-ignore
                    mm.push(<option value={index + "_" + key}>{value[key]}</option>);
                });
                sState.setState({selectData: mm});
            } else {
                console.log(response)
            }
        }).catch(function (response) {
            console.log(response)
        });
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        await this.balance();
    }

    async balance() {
        let ref = this;
        let selected = new Set<number>();
        let selectedData = new Array<string>();
        // @ts-ignore
        for (let option of document.getElementsByTagName("select")[0].options) {
            if (option.selected) {
                selectedData.push(option.value);
                selected.add(Number.parseInt(option.value.split("_")[1]));
            }
        }
        console.log(selected)
        try {
            const response = await axios.post("/api/v1/balance", {
                userIds: Array.from(selected),
            }, {
                responseType: "json",
            });
            if (response.status === 200) {
                let team1List = new Array<JSX.Element>();
                let team2List = new Array<JSX.Element>();
                response.data["api"]["userList"].forEach(function (value: Object, index: number) {
                    // @ts-ignore
                    if (Number.parseInt(value["team"]) === 1) {
                        // @ts-ignore
                        team1List.push(ref.buildMultiDataRow(ref.getPositionForId(Number.parseInt(value["position"])), value["name"], value["positionSr"], true, true, true));
                    } else {
                        // @ts-ignore
                        team2List.push(ref.buildMultiDataRow(ref.getPositionForId(Number.parseInt(value["position"])), value["name"], value["positionSr"], true, true, true));
                    }
                });
                while (team1List.length < 6) {
                    team1List.push(ref.buildBlankTableRow());
                }
                while (team2List.length < 6) {
                    team2List.push(ref.buildBlankTableRow());
                }
                this.setState({table1Rows: team1List, table2Rows: team2List, selectedSelectData: selectedData});
            }
        } catch (e) {
            let message = "";
            if (e.message.endsWith("409")) {
                message = "Invalid username or password"
            } else {
                message = "Invalid request"
            }
            // @ts-ignore
            Notific8.create(message, {themeColor: 'ruby', life: 4000}).then((notification) => {
                // open the notification
                notification.open();
            });
        }
    }

    render() {
        return (
            <div className="container container-mod">
                <Select multiple={true} value={this.state.selectedSelectData} filter={true}>
                        {this.state.selectData}
                </Select>
                <form onSubmit={(e) => this.handleSubmit(e)}>
                    <Button cls="success form-group form-control" title="Balance" type="submit"/>
                </form>
                <div className="grid m-0">
                    <div className="row">
                        <div className="cell-6">
                            <table className="table striped table-border mt-4" data-role="table">
                                <thead>
                                <tr>
                                    <th>Role</th>
                                    <th>Username</th>
                                    <th>Sr</th>
                                    <th>Tank</th>
                                    <th>Dps</th>
                                    <th>Support</th>
                                </tr>
                                </thead>
                                <tbody>
                                {this.state.table1Rows}
                                </tbody>
                            </table>
                        </div>
                        <div className="cell-6">
                            <table className="table striped table-border mt-4" data-role="table">
                                <thead>
                                <tr>
                                    <th>Role</th>
                                    <th>Username</th>
                                    <th>Sr</th>
                                    <th>Tank</th>
                                    <th>Dps</th>
                                    <th>Support</th>
                                </tr>
                                </thead>
                                <tbody>
                                {this.state.table2Rows}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default Home;

class BalanceTablePage {
    table1Rows: Array<JSX.Element>;
    table1MetaRows: Array<JSX.Element>;
    table2Rows: Array<JSX.Element>;
    table2MetaRows: Array<JSX.Element>;


    constructor(table1Rows: Array<JSX.Element>, table1MetaRows: Array<JSX.Element>, table2Rows: Array<JSX.Element>, table2MetaRows: Array<JSX.Element>) {
        this.table1Rows = table1Rows;
        this.table1MetaRows = table1MetaRows;
        this.table2Rows = table2Rows;
        this.table2MetaRows = table2MetaRows;
    }
}

export default BalanceTablePage